package com.mytech.mangatalkreader.service

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents the current state of a chapter download.
 */
sealed class DownloadProgress {
    data class Queued(val chapterTitle: String) : DownloadProgress()
    data class FetchingPageUrls(val chapterTitle: String) : DownloadProgress()
    data class DownloadingImage(
        val chapterTitle: String,
        val currentPage: Int,
        val totalPages: Int,
        val bytesDownloaded: Long,
        val totalBytes: Long
    ) : DownloadProgress()
    data class Completed(val chapterTitle: String, val uri: Uri) : DownloadProgress()
    data class Failed(val chapterTitle: String, val error: Throwable) : DownloadProgress()
}

/**
 * Service responsible for downloading manga chapters and their page images.
 *
 * Uses OkHttp for all HTTP requests and saves downloaded images to the
 * app's private files directory (which requires no storage permissions).
 *
 * Download progress is exposed as a Kotlin [Flow] so UI components can
 * observe and display progress bars in real time.
 *
 * Typical usage:
 *   mangaDownloadService.downloadChapter(chapterUrl, "Chapter 42")
 *       .collect { progress -> updateUi(progress) }
 */
@Singleton
class MangaDownloadService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val chapterDao: ChapterDao
) {

    /** Base directory inside the app's private files area for manga downloads. */
    private val downloadsDir by lazy {
        File(context.filesDir, "manga_downloads").apply { mkdirs() }
    }

    /**
     * Download an entire manga chapter.
     *
     * @param chapterUrl  URL that returns a JSON array of page image URLs,
     *                    or a URL from which we can scrape image URLs.
     *                    For this implementation we assume it returns plain text
     *                    with one image URL per line.
     * @param chapterTitle  Human-readable title (used for directory naming).
     * @param chapterId     Optional database row ID. If provided, the chapter's
     *                      download status is updated in the Room database.
     *
     * @return A [Flow] of [DownloadProgress] events culminating in either
     *         [DownloadProgress.Completed] or [DownloadProgress.Failed].
     */
    fun downloadChapter(
        chapterUrl: String,
        chapterTitle: String,
        chapterId: Long? = null
    ): Flow<DownloadProgress> = flow {

        emit(DownloadProgress.Queued(chapterTitle))

        try {
            // Step 1: Fetch the list of page image URLs from the chapter URL.
            emit(DownloadProgress.FetchingPageUrls(chapterTitle))
            val pageUrls = fetchPageUrls(chapterUrl)

            if (pageUrls.isEmpty()) {
                throw IllegalStateException("No page URLs found at $chapterUrl")
            }

            // Step 2: Create a directory for this chapter.
            val safeTitle = sanitizeFileName(chapterTitle)
            val chapterDir = File(downloadsDir, safeTitle).apply { mkdirs() }

            var totalBytesExpected = 0L
            val downloadedFiles = mutableListOf<File>()

            // Step 3: Download each page image.
            for ((index, pageUrl) in pageUrls.withIndex()) {
                val currentPage = index + 1
                val fileName = "page_${String.format("%03d", currentPage)}.jpg"
                val outputFile = File(chapterDir, fileName)

                val bytesDownloaded = downloadImage(pageUrl, outputFile) { bytes, total ->
                    // Emit per-image progress
                    emit(
                        DownloadProgress.DownloadingImage(
                            chapterTitle = chapterTitle,
                            currentPage = currentPage,
                            totalPages = pageUrls.size,
                            bytesDownloaded = bytes,
                            totalBytes = total
                        )
                    )
                }

                totalBytesExpected += bytesDownloaded
                downloadedFiles.add(outputFile)
            }

            // Step 4: Update the Room database if a chapterId was provided.
            chapterId?.let { id ->
                chapterDao.updateDownloadStatus(
                    chapterId = id,
                    isDownloaded = true,
                    downloadPath = chapterDir.absolutePath
                )
            }

            // Step 5: Build a content URI for the chapter directory.
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                chapterDir
            )

            emit(DownloadProgress.Completed(chapterTitle, uri))

        } catch (e: Exception) {
            emit(DownloadProgress.Failed(chapterTitle, e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Convenience suspend function that returns a single [Result].
     * Useful when you only care about the final outcome and not intermediate progress.
     */
    suspend fun downloadChapterResult(
        chapterUrl: String,
        chapterTitle: String,
        chapterId: Long? = null
    ): Result<Uri> = withContext(Dispatchers.IO) {
        var finalUri: Uri? = null
        var exception: Exception? = null

        downloadChapter(chapterUrl, chapterTitle, chapterId).collect { progress ->
            when (progress) {
                is DownloadProgress.Completed -> finalUri = progress.uri
                is DownloadProgress.Failed -> exception = progress.error as Exception
                else -> Unit
            }
        }

        finalUri?.let { Result.success(it) }
            ?: Result.failure(exception ?: IllegalStateException("Download completed without URI"))
    }

    /**
     * Delete a previously downloaded chapter.
     *
     * @param chapterId  The database row ID of the chapter.
     */
    suspend fun deleteDownloadedChapter(chapterId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return@withContext Result.failure<Unit>(
                    Exception("Chapter $chapterId not found in database")
                )

            chapter.downloadPath?.let { path ->
                val dir = File(path)
                if (dir.exists() && dir.isDirectory) {
                    dir.deleteRecursively()
                }
            }

            chapterDao.updateDownloadStatus(
                chapterId = chapterId,
                isDownloaded = false,
                downloadPath = null
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Return a list of all downloaded chapter directories.
     */
    fun listDownloadedChapters(): List<File> {
        return downloadsDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

    /**
     * Calculate the total disk size of all downloaded manga.
     */
    fun getTotalDownloadSizeBytes(): Long {
        return downloadsDir.listFiles()?.sumOf { dir ->
            dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } ?: 0L
    }

    // --------------- Internal helpers ---------------

    /**
     * Fetch page image URLs from the given chapter URL.
     *
     * This implementation expects the response body to be plain text with
     * one image URL per line. Adapt this method if your source returns
     * JSON, HTML, or another format.
     */
    private suspend fun fetchPageUrls(chapterUrl: String): List<String> {
        val request = Request.Builder()
            .url(chapterUrl)
            .get()
            .build()

        return okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "Failed to fetch page URLs from $chapterUrl: ${response.code} ${response.message}"
                )
            }
            response.body?.string()
                ?.lineSequence()
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() && (it.startsWith("http://") || it.startsWith("https://")) }
                ?.toList()
                ?: emptyList()
        }
    }

    /**
     * Download a single image from [imageUrl] to [outputFile].
     *
     * The [progressCallback] is invoked periodically with the number of bytes
     * downloaded so far and the total content length (if known, else -1).
     *
     * @return The total number of bytes written to the file.
     */
    private suspend fun downloadImage(
        imageUrl: String,
        outputFile: File,
        progressCallback: suspend (bytesDownloaded: Long, totalBytes: Long) -> Unit
    ): Long = withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url(imageUrl)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to download image from $imageUrl: ${response.code} ${response.message}"
            )
        }

        val body = response.body
            ?: throw IllegalStateException("Response body is null for $imageUrl")

        val contentLength = body.contentLength()
        var totalBytesRead = 0L

        outputFile.sink().buffer().use { sink ->
            val buffer = okio.Buffer()
            var bytesRead: Long
            while (body.source().read(buffer, 8192).also { bytesRead = it } != -1L) {
                sink.write(buffer, bytesRead)
                totalBytesRead += bytesRead

                // Emit progress every ~256 KB
                if (totalBytesRead % (256 * 1024) < 8192) {
                    progressCallback(totalBytesRead, contentLength)
                }
            }
            sink.flush()
        }

        // Final progress emission
        progressCallback(totalBytesRead, contentLength)

        totalBytesRead
    }

    /**
     * Remove characters that are invalid for file names on Android.
     */
    private fun sanitizeFileName(name: String): String {
        return name
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .take(100) // Limit length
    }
}
