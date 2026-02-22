package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Insert
    suspend fun insert(manga: MangaEntity): Long

    @Update
    suspend fun update(manga: MangaEntity)

    @Delete
    suspend fun delete(manga: MangaEntity)

    @Query("SELECT * FROM manga WHERE id = :id")
    suspend fun getMangaById(id: Long): MangaEntity?

    @Query("SELECT * FROM manga ORDER BY dateAdded DESC")
    fun getAllManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE title LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchManga(query: String): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE isRead = 0 ORDER BY dateAdded DESC")
    fun getUnreadManga(): Flow<List<MangaEntity>>

    @Query("DELETE FROM manga WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE manga SET progress = :progress, lastReadDate = datetime('now') WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Float)

    @Query("UPDATE manga SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: Long, isRead: Boolean)

    @Query("UPDATE manga SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
