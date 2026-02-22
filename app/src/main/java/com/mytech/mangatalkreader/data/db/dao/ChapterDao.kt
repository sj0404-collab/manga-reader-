package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert
    suspend fun insert(chapter: ChapterEntity): Long

    @Update
    suspend fun update(chapter: ChapterEntity)

    @Delete
    suspend fun delete(chapter: ChapterEntity)

    @Query("SELECT * FROM chapter WHERE id = :id")
    suspend fun getChapterById(id: Long): ChapterEntity?

    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId ORDER BY number ASC")
    fun getChaptersByManga(mangaId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId AND number = :chapterNumber")
    suspend fun getChapterByNumber(mangaId: Long, chapterNumber: Int): ChapterEntity?

    @Query("UPDATE chapter SET currentPage = :page WHERE id = :id")
    suspend fun updateCurrentPage(id: Long, page: Int)

    @Query("UPDATE chapter SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: Long, isRead: Boolean)

    @Query("DELETE FROM chapter WHERE mangaId = :mangaId")
    suspend fun deleteByMangaId(mangaId: Long)
}
