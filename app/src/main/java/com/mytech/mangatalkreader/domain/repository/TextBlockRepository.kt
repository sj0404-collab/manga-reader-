package com.mytech.mangatalkreader.domain.repository

import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity
import kotlinx.coroutines.flow.Flow

interface TextBlockRepository {

    suspend fun insert(textBlock: TextBlockEntity): Long

    suspend fun insertAll(textBlocks: List<TextBlockEntity>): List<Long>

    suspend fun update(textBlock: TextBlockEntity): Int

    suspend fun delete(textBlock: TextBlockEntity): Int

    suspend fun deleteById(blockId: Long): Int

    suspend fun deleteAllByChapterId(chapterId: Long): Int

    suspend fun deleteAllByPage(chapterId: Long, pageNumber: Int): Int

    suspend fun getTextBlockById(blockId: Long): TextBlockEntity?

    fun getTextBlockByIdAsFlow(blockId: Long): Flow<TextBlockEntity?>

    fun getTextBlocksByPage(chapterId: Long, pageNumber: Int): Flow<List<TextBlockEntity>>

    suspend fun getTextBlocksByPageSync(chapterId: Long, pageNumber: Int): List<TextBlockEntity>

    fun getTextBlocksByChapter(chapterId: Long): Flow<List<TextBlockEntity>>

    suspend fun getTextBlocksByChapterSync(chapterId: Long): List<TextBlockEntity>

    suspend fun getUntranslatedBlocksByPage(chapterId: Long, page: Int): List<TextBlockEntity>

    fun getTextBlockCountByChapter(chapterId: Long): Flow<Int>

    fun getTranslatedBlockCountByChapter(chapterId: Long): Flow<Int>

    suspend fun getDistinctPageNumbers(chapterId: Long): List<Int>

    suspend fun updateOriginalText(blockId: Long, text: String, updatedAt: Long = System.currentTimeMillis()): Int

    suspend fun updateTranslatedText(blockId: Long, text: String, updatedAt: Long = System.currentTimeMillis()): Int

    suspend fun updateBothTexts(blockId: Long, originalText: String, translatedText: String, updatedAt: Long = System.currentTimeMillis()): Int

    fun getTextBlocksByLanguage(chapterId: Long, language: String): Flow<List<TextBlockEntity>>

    fun searchTextBlocks(chapterId: Long, query: String): Flow<List<TextBlockEntity>>
}
