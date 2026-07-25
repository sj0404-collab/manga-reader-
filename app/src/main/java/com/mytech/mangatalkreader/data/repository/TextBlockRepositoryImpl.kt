package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.db.dao.TextBlockDao
import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity
import com.mytech.mangatalkreader.domain.repository.TextBlockRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TextBlockRepositoryImpl(
    private val textBlockDao: TextBlockDao,
    private val applicationScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) : TextBlockRepository {

    override suspend fun insert(textBlock: TextBlockEntity): Long =
        withContext(ioDispatcher) { textBlockDao.insert(textBlock) }

    override suspend fun insertAll(textBlocks: List<TextBlockEntity>): List<Long> =
        withContext(ioDispatcher) { textBlockDao.insertAll(textBlocks) }

    override suspend fun update(textBlock: TextBlockEntity): Int =
        withContext(ioDispatcher) { textBlockDao.update(textBlock) }

    override suspend fun delete(textBlock: TextBlockEntity): Int =
        withContext(ioDispatcher) { textBlockDao.delete(textBlock) }

    override suspend fun deleteById(blockId: Long): Int =
        withContext(ioDispatcher) { textBlockDao.deleteById(blockId) }

    override suspend fun deleteAllByChapterId(chapterId: Long): Int =
        withContext(ioDispatcher) { textBlockDao.deleteAllByChapterId(chapterId) }

    override suspend fun deleteAllByPage(chapterId: Long, pageNumber: Int): Int =
        withContext(ioDispatcher) { textBlockDao.deleteAllByPage(chapterId, pageNumber) }

    override suspend fun getTextBlockById(blockId: Long): TextBlockEntity? =
        withContext(ioDispatcher) { textBlockDao.getTextBlockById(blockId) }

    override fun getTextBlockByIdAsFlow(blockId: Long): Flow<TextBlockEntity?> =
        textBlockDao.getTextBlockByIdAsFlow(blockId)

    override fun getTextBlocksByPage(chapterId: Long, pageNumber: Int): Flow<List<TextBlockEntity>> =
        textBlockDao.getTextBlocksByPage(chapterId, pageNumber)

    override suspend fun getTextBlocksByPageSync(chapterId: Long, pageNumber: Int): List<TextBlockEntity> =
        withContext(ioDispatcher) { textBlockDao.getTextBlocksByPageSync(chapterId, pageNumber) }

    override fun getTextBlocksByChapter(chapterId: Long): Flow<List<TextBlockEntity>> =
        textBlockDao.getTextBlocksByChapter(chapterId)

    override suspend fun getTextBlocksByChapterSync(chapterId: Long): List<TextBlockEntity> =
        withContext(ioDispatcher) { textBlockDao.getTextBlocksByChapterSync(chapterId) }

    override suspend fun getUntranslatedBlocksByPage(chapterId: Long, page: Int): List<TextBlockEntity> =
        withContext(ioDispatcher) { textBlockDao.getUntranslatedBlocksByPage(chapterId, page) }

    override fun getTextBlockCountByChapter(chapterId: Long): Flow<Int> =
        textBlockDao.getTextBlockCountByChapter(chapterId)

    override fun getTranslatedBlockCountByChapter(chapterId: Long): Flow<Int> =
        textBlockDao.getTranslatedBlockCountByChapter(chapterId)

    override suspend fun getDistinctPageNumbers(chapterId: Long): List<Int> =
        withContext(ioDispatcher) { textBlockDao.getDistinctPageNumbers(chapterId) }

    override suspend fun updateOriginalText(blockId: Long, text: String, updatedAt: Long): Int =
        withContext(ioDispatcher) { textBlockDao.updateOriginalText(blockId, text, updatedAt) }

    override suspend fun updateTranslatedText(blockId: Long, text: String, updatedAt: Long): Int =
        withContext(ioDispatcher) { textBlockDao.updateTranslatedText(blockId, text, updatedAt) }

    override suspend fun updateBothTexts(blockId: Long, originalText: String, translatedText: String, updatedAt: Long): Int =
        withContext(ioDispatcher) { textBlockDao.updateBothTexts(blockId, originalText, translatedText, updatedAt) }

    override fun getTextBlocksByLanguage(chapterId: Long, language: String): Flow<List<TextBlockEntity>> =
        textBlockDao.getTextBlocksByLanguage(chapterId, language)

    override fun searchTextBlocks(chapterId: Long, query: String): Flow<List<TextBlockEntity>> =
        textBlockDao.searchTextBlocks(chapterId, query)
}
