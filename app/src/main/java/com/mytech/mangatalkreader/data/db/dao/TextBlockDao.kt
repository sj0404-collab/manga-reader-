package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextBlockDao {
    @Insert
    suspend fun insert(textBlock: TextBlockEntity): Long

    @Update
    suspend fun update(textBlock: TextBlockEntity)

    @Delete
    suspend fun delete(textBlock: TextBlockEntity)

    @Query("SELECT * FROM text_block WHERE chapterId = :chapterId AND pageNumber = :pageNumber ORDER BY y ASC, x ASC")
    fun getTextBlocksByPage(chapterId: Long, pageNumber: Int): Flow<List<TextBlockEntity>>

    @Query("SELECT * FROM text_block WHERE chapterId = :chapterId ORDER BY pageNumber ASC, y ASC, x ASC")
    fun getTextBlocksByChapter(chapterId: Long): Flow<List<TextBlockEntity>>

    @Query("SELECT * FROM text_block WHERE id = :id")
    suspend fun getTextBlockById(id: Long): TextBlockEntity?

    @Query("DELETE FROM text_block WHERE chapterId = :chapterId")
    suspend fun deleteByChapterId(chapterId: Long)

    @Query("DELETE FROM text_block WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE text_block SET hasAudio = :hasAudio, audioPath = :audioPath WHERE id = :id")
    suspend fun updateAudioStatus(id: Long, hasAudio: Boolean, audioPath: String?)
}
