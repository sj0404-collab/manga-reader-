package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import com.mytech.mangatalkreader.domain.repository.ChapterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepositoryImpl @Inject constructor(
    private val chapterDao: ChapterDao,
    private val applicationScope: CoroutineScope,
    @com.mytech.mangatalkreader.di.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ChapterRepository {

    override fun getChaptersByMangaId(mangaId: Long): Flow<List<ChapterEntity>> =
        chapterDao.getChaptersByMangaId(mangaId).flowOn(ioDispatcher)

    override fun getChaptersByMangaIdAsc(mangaId: Long): Flow<List<ChapterEntity>> =
        chapterDao.getChaptersByMangaIdAsc(mangaId).flowOn(ioDispatcher)

    override fun getChapterByIdAsFlow(chapterId: Long): Flow<ChapterEntity?> =
        chapterDao.getChapterByIdAsFlow(chapterId).flowOn(ioDispatcher)

    override fun getDownloadedChapters(): Flow<List<ChapterEntity>> =
        chapterDao.getDownloadedChapters().flowOn(ioDispatcher)

    override fun getChapterCountByMangaId(mangaId: Long): Flow<Int> =
        chapterDao.getChapterCountByMangaId(mangaId).flowOn(ioDispatcher)

    override fun getDownloadedChapterCountByMangaId(mangaId: Long): Flow<Int> =
        chapterDao.getDownloadedChapterCountByMangaId(mangaId).flowOn(ioDispatcher)

    override fun getReadChapterCountByMangaId(mangaId: Long): Flow<Int> =
        chapterDao.getReadChapterCountByMangaId(mangaId).flowOn(ioDispatcher)

    override suspend fun insert(chapter: ChapterEntity): Long =
        withContext(ioDispatcher) { chapterDao.insert(chapter) }

    override suspend fun insertAll(chapters: List<ChapterEntity>): List<Long> =
        withContext(ioDispatcher) { chapterDao.insertAll(chapters) }

    override suspend fun update(chapter: ChapterEntity): Int =
        withContext(ioDispatcher) { chapterDao.update(chapter) }

    override suspend fun delete(chapter: ChapterEntity): Int =
        withContext(ioDispatcher) { chapterDao.delete(chapter) }

    override suspend fun deleteById(chapterId: Long): Int =
        withContext(ioDispatcher) { chapterDao.deleteById(chapterId) }

    override suspend fun deleteAllByMangaId(mangaId: Long): Int =
        withContext(ioDispatcher) { chapterDao.deleteAllByMangaId(mangaId) }

    override suspend fun getChapterById(chapterId: Long): ChapterEntity? =
        withContext(ioDispatcher) { chapterDao.getChapterById(chapterId) }

    override suspend fun getChapterByNumber(mangaId: Long, chapterNumber: Float): ChapterEntity? =
        withContext(ioDispatcher) { chapterDao.getChapterByNumber(mangaId, chapterNumber) }

    override suspend fun getNextUnreadChapter(mangaId: Long): ChapterEntity? =
        withContext(ioDispatcher) { chapterDao.getNextUnreadChapter(mangaId) }

    override suspend fun setReadStatus(chapterId: Long, isRead: Boolean): Int =
        withContext(ioDispatcher) { chapterDao.setReadStatus(chapterId, isRead) }

    override suspend fun updateReadProgress(chapterId: Long, page: Int): Int =
        withContext(ioDispatcher) { chapterDao.updateReadProgress(chapterId, page) }

    override suspend fun updateDownloadStatus(chapterId: Long, isDownloaded: Boolean, downloadPath: String?): Int =
        withContext(ioDispatcher) { chapterDao.updateDownloadStatus(chapterId, isDownloaded, downloadPath) }

    override suspend fun getLatestChapterNumber(mangaId: Long): Float? =
        withContext(ioDispatcher) { chapterDao.getLatestChapterNumber(mangaId) }

    override suspend fun getChapterBySourceId(sourceChapterId: String, mangaId: Long): ChapterEntity? =
        withContext(ioDispatcher) { chapterDao.getChapterBySourceId(sourceChapterId, mangaId) }

    override suspend fun getChaptersInRange(mangaId: Long, fromChapter: Float, toChapter: Float): List<ChapterEntity> =
        withContext(ioDispatcher) { chapterDao.getChaptersInRange(mangaId, fromChapter, toChapter) }
}
