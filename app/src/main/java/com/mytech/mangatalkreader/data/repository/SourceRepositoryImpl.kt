package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.source.SourceRepository
import com.mytech.mangatalkreader.data.source.model.ChapterInfo
import com.mytech.mangatalkreader.data.source.model.MangaDetailInfo
import com.mytech.mangatalkreader.data.source.model.MangaInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepositoryImpl @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val applicationScope: CoroutineScope,
    @com.mytech.mangatalkreader.di.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : com.mytech.mangatalkreader.domain.repository.SourceRepository {

    override fun getAllSourceIds(): List<String> =
        sourceRepository.getAllSources().map { it.id }

    override suspend fun getPopularManga(sourceId: String, page: Int): List<MangaInfo> =
        withContext(ioDispatcher) {
            val source = sourceRepository.getSource(sourceId)
                ?: throw IllegalArgumentException("Source not found: $sourceId")
            source.getPopularManga(page)
        }

    override suspend fun searchManga(sourceId: String, query: String, page: Int): List<MangaInfo> =
        withContext(ioDispatcher) {
            val source = sourceRepository.getSource(sourceId)
                ?: throw IllegalArgumentException("Source not found: $sourceId")
            source.searchManga(query, page)
        }

    override suspend fun getMangaDetails(sourceId: String, mangaId: String): MangaDetailInfo =
        withContext(ioDispatcher) {
            val source = sourceRepository.getSource(sourceId)
                ?: throw IllegalArgumentException("Source not found: $sourceId")
            source.getMangaDetails(mangaId)
        }

    override suspend fun getChapterList(sourceId: String, mangaId: String): List<ChapterInfo> =
        withContext(ioDispatcher) {
            val source = sourceRepository.getSource(sourceId)
                ?: throw IllegalArgumentException("Source not found: $sourceId")
            source.getChapterList(mangaId)
        }

    override suspend fun getPageList(sourceId: String, chapterId: String): List<String> =
        withContext(ioDispatcher) {
            val source = sourceRepository.getSource(sourceId)
                ?: throw IllegalArgumentException("Source not found: $sourceId")
            source.getPageList(chapterId)
        }
}
