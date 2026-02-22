package com.mytech.mangatalkreader.domain.service

import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveService @Inject constructor() {

    suspend fun extractPages(archivePath: String, outputDir: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val zipFile = ZipFile(archivePath)
            val imageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")
            val extractedFiles = mutableListOf<String>()

            zipFile.fileHeaders.filter { header ->
                imageExtensions.any { ext ->
                    header.fileName.lowercase().endsWith(ext)
                }
            }.sortedBy { it.fileName }.forEach { header ->
                val outputFile = File(outputDir, header.fileName.substringAfterLast("/"))
                if (!outputFile.exists()) {
                    val inputStream = zipFile.getInputStream(header)
                    outputFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                }
                extractedFiles.add(outputFile.absolutePath)
            }

            Timber.d("Extracted ${extractedFiles.size} pages from archive")
            extractedFiles
        } catch (e: Exception) {
            Timber.e(e, "Error extracting archive")
            emptyList()
        }
    }

    suspend fun getArchiveInfo(archivePath: String): ArchiveInfo = withContext(Dispatchers.IO) {
        try {
            val zipFile = ZipFile(archivePath)
            val imageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")
            val imageCount = zipFile.fileHeaders.count { header ->
                imageExtensions.any { ext ->
                    header.fileName.lowercase().endsWith(ext)
                }
            }
            val firstImagePath = zipFile.fileHeaders.find { header ->
                imageExtensions.any { ext ->
                    header.fileName.lowercase().endsWith(ext)
                }
            }?.fileName

            ArchiveInfo(
                totalSize = zipFile.file.length(),
                pageCount = imageCount,
                firstImageName = firstImagePath
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting archive info")
            ArchiveInfo(0L, 0, null)
        }
    }

    suspend fun extractFirstPage(archivePath: String, outputDir: String): String? = withContext(Dispatchers.IO) {
        try {
            val zipFile = ZipFile(archivePath)
            val imageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")
            val firstImageHeader = zipFile.fileHeaders.find { header ->
                imageExtensions.any { ext ->
                    header.fileName.lowercase().endsWith(ext)
                }
            }

            if (firstImageHeader != null) {
                val outputFile = File(outputDir, firstImageHeader.fileName.substringAfterLast("/"))
                val inputStream = zipFile.getInputStream(firstImageHeader)
                outputFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                return@withContext outputFile.absolutePath
            }
            null
        } catch (e: Exception) {
            Timber.e(e, "Error extracting first page")
            null
        }
    }

    fun getImageList(folderPath: String): List<String> {
        try {
            val folder = File(folderPath)
            if (!folder.isDirectory) return emptyList()

            val imageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")
            return folder.listFiles()?.filter { file ->
                imageExtensions.any { ext ->
                    file.name.lowercase().endsWith(ext)
                }
            }?.sortedBy { it.name }?.map { it.absolutePath } ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Error listing images")
            return emptyList()
        }
    }

    data class ArchiveInfo(
        val totalSize: Long,
        val pageCount: Int,
        val firstImageName: String?
    )
}
