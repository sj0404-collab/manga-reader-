package com.mytech.mangatalkreader.domain.repository

import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    fun getChaptersByMangaId(mangaId: Long): Flow<List<ChapterEntity>>
    fun getChaptersByMangaIdAsc(mangaId: Long): Flow<List<ChapterEntity>>
    fun getChapterByIdAsFlow(chapterId: Long): Flow<ChapterEntity?>
    fun getDownloadedChapters(): Flow<List<ChapterEntity>>
    fun getChapterCountByMangaId(mangaId: Long): Flow<Int>
    fun getDownloadedChapterCountByMangaId(mangaId: Long): Flow<Int>
    fun getReadChapterCountByMangaId(mangaId: Long): Flow<Int>

    suspend fun insert(chapter: ChapterEntity): Long
    suspend fun insertAll(chapters: List<ChapterEntity>): List<Long>
    suspend fun update(chapter: ChapterEntity): Int
    suspend fun delete(chapter: ChapterEntity): Int
    suspend fun deleteById(chapterId: Long): Int
    suspend fun deleteAllByMangaId(mangaId: Long): Int
    suspend fun getChapterById(chapterId: Long): ChapterEntity?
    suspend fun getChapterByNumber(mangaId: Long, chapterNumber: Float): ChapterEntity?
    suspend fun getNextUnreadChapter(mangaId: Long): ChapterEntity?
    suspend fun setReadStatus(chapterId: Long, isRead: Boolean): Int
    suspend fun updateReadProgress(chapterId: Long, page: Int): Int
    suspend fun updateDownloadStatus(chapterId: Long, isDownloaded: Boolean, downloadPath: String?): Int
    suspend fun getLatestChapterNumber(mangaId: Long): Float?
    suspend fun getChapterBySourceId(sourceChapterId: String, mangaId: Long): ChapterEntity?
    suspend fun getChaptersInRange(mangaId: Long, fromChapter: Float, toChapter: Float): List<ChapterEntity>
}
