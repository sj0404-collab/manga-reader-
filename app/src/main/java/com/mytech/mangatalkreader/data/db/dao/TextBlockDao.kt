package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(textBlock: TextBlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(textBlocks: List<TextBlockEntity>): List<Long>

    @Update
    suspend fun update(textBlock: TextBlockEntity): Int

    @Delete
    suspend fun delete(textBlock: TextBlockEntity): Int

    @Query("DELETE FROM text_block WHERE id = :blockId")
    suspend fun deleteById(blockId: Long): Int

    @Query("DELETE FROM text_block WHERE chapter_id = :chapterId")
    suspend fun deleteAllByChapterId(chapterId: Long): Int

    @Query("DELETE FROM text_block WHERE chapter_id = :chapterId AND page_number = :pageNumber")
    suspend fun deleteAllByPage(chapterId: Long, pageNumber: Int): Int

    @Query("SELECT * FROM text_block WHERE id = :blockId")
    suspend fun getTextBlockById(blockId: Long): TextBlockEntity?

    @Query("SELECT * FROM text_block WHERE id = :blockId")
    fun getTextBlockByIdAsFlow(blockId: Long): Flow<TextBlockEntity?>

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId AND page_number = :pageNumber ORDER BY block_index ASC")
    fun getTextBlocksByPage(chapterId: Long, pageNumber: Int): Flow<List<TextBlockEntity>>

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId AND page_number = :pageNumber ORDER BY block_index ASC")
    suspend fun getTextBlocksByPageSync(chapterId: Long, pageNumber: Int): List<TextBlockEntity>

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId ORDER BY page_number ASC, block_index ASC")
    fun getTextBlocksByChapter(chapterId: Long): Flow<List<TextBlockEntity>>

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId ORDER BY page_number ASC, block_index ASC")
    suspend fun getTextBlocksByChapterSync(chapterId: Long): List<TextBlockEntity>

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId AND page_number = :page AND is_translated = 0 ORDER BY block_index ASC")
    suspend fun getUntranslatedBlocksByPage(chapterId: Long, page: Int): List<TextBlockEntity>

    @Query("SELECT COUNT(*) FROM text_block WHERE chapter_id = :chapterId")
    fun getTextBlockCountByChapter(chapterId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM text_block WHERE chapter_id = :chapterId AND is_translated = 1")
    fun getTranslatedBlockCountByChapter(chapterId: Long): Flow<Int>

    @Query("SELECT DISTINCT page_number FROM text_block WHERE chapter_id = :chapterId ORDER BY page_number ASC")
    suspend fun getDistinctPageNumbers(chapterId: Long): List<Int>

    @Query("UPDATE text_block SET original_text = :text, is_edited = 1, updated_at = :updatedAt WHERE id = :blockId")
    suspend fun updateOriginalText(blockId: Long, text: String, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE text_block SET translated_text = :text, is_translated = 1, updated_at = :updatedAt WHERE id = :blockId")
    suspend fun updateTranslatedText(blockId: Long, text: String, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE text_block SET translated_text = :translatedText, original_text = :originalText, is_translated = 1, is_edited = 1, updated_at = :updatedAt WHERE id = :blockId")
    suspend fun updateBothTexts(blockId: Long, originalText: String, translatedText: String, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("SELECT * FROM text_block WHERE chapter_id = :chapterId AND source_language = :language ORDER BY page_number ASC, block_index ASC")
    fun getTextBlocksByLanguage(chapterId: Long, language: String): Flow<List<TextBlockEntity>>

    @Query("SELECT * FROM text_block WHERE original_text LIKE '%' || :query || '%' AND chapter_id = :chapterId ORDER BY page_number ASC, block_index ASC")
    fun searchTextBlocks(chapterId: Long, query: String): Flow<List<TextBlockEntity>>
}
