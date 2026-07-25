package com.mytech.mangatalkreader.domain.model

import com.mytech.mangatalkreader.data.db.entity.ChapterEntity

data class Chapter(
    val id: Long = 0,
    val mangaId: Long,
    val sourceChapterId: String,
    val chapterNumber: Float,
    val title: String = "",
    val pageCount: Int = 0,
    val pagesJson: String = "[]",
    val isDownloaded: Boolean = false,
    val downloadPath: String? = null,
    val isRead: Boolean = false,
    val lastReadPage: Int = 0,
    val fetchedDate: Long = System.currentTimeMillis(),
    val uploadDate: Long? = null,
    val scanlator: String = ""
) {
    fun toEntity(): ChapterEntity {
        return ChapterEntity(
            id = id,
            mangaId = mangaId,
            sourceChapterId = sourceChapterId,
            chapterNumber = chapterNumber,
            title = title,
            pageCount = pageCount,
            pagesJson = pagesJson,
            isDownloaded = isDownloaded,
            downloadPath = downloadPath,
            isRead = isRead,
            lastReadPage = lastReadPage,
            fetchedDate = fetchedDate,
            uploadDate = uploadDate,
            scanlator = scanlator
        )
    }

    companion object {
        fun fromEntity(entity: ChapterEntity): Chapter {
            return Chapter(
                id = entity.id,
                mangaId = entity.mangaId,
                sourceChapterId = entity.sourceChapterId,
                chapterNumber = entity.chapterNumber,
                title = entity.title,
                pageCount = entity.pageCount,
                pagesJson = entity.pagesJson,
                isDownloaded = entity.isDownloaded,
                downloadPath = entity.downloadPath,
                isRead = entity.isRead,
                lastReadPage = entity.lastReadPage,
                fetchedDate = entity.fetchedDate,
                uploadDate = entity.uploadDate,
                scanlator = entity.scanlator
            )
        }
    }
}
