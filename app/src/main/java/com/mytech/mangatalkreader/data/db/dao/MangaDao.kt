package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(manga: MangaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mangaList: List<MangaEntity>): List<Long>

    @Update
    suspend fun update(manga: MangaEntity): Int

    @Delete
    suspend fun delete(manga: MangaEntity): Int

    @Query("DELETE FROM manga WHERE id = :mangaId")
    suspend fun deleteById(mangaId: Long): Int

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    suspend fun getMangaById(mangaId: Long): MangaEntity?

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    fun getMangaByIdAsFlow(mangaId: Long): Flow<MangaEntity?>

    @Query("SELECT * FROM manga WHERE source_manga_id = :sourceMangaId AND source_id = :sourceId LIMIT 1")
    suspend fun getMangaBySourceId(sourceMangaId: String, sourceId: Long): MangaEntity?

    /** Default: order by last read, then by date added */
    @Query("SELECT * FROM manga ORDER BY last_read_date DESC, added_date DESC")
    fun getAllManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY last_read_date DESC")
    fun getAllMangaByLastRead(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY title ASC")
    fun getAllMangaByTitle(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY added_date DESC")
    fun getAllMangaByDateAdded(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE is_favorite = 1 ORDER BY last_read_date DESC")
    fun getFavoriteManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE last_read_date IS NOT NULL ORDER BY last_read_date DESC LIMIT :limit")
    fun getRecentlyReadManga(limit: Int = 10): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE source_id = :sourceId ORDER BY title ASC")
    fun getMangaBySourceId(sourceId: Long): Flow<List<MangaEntity>>

    @Query("SELECT COUNT(*) FROM manga")
    fun getMangaCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM manga WHERE is_favorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("UPDATE manga SET is_favorite = :isFavorite WHERE id = :mangaId")
    suspend fun setFavorite(mangaId: Long, isFavorite: Boolean): Int

    @Query("UPDATE manga SET last_read_chapter_id = :chapterId, last_read_page = :page, last_read_date = :readDate WHERE id = :mangaId")
    suspend fun updateReadProgress(mangaId: Long, chapterId: Long, page: Int, readDate: Long): Int

    @Query("SELECT * FROM manga WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchManga(query: String): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE status = :status ORDER BY title ASC")
    fun getMangaByStatus(status: String): Flow<List<MangaEntity>>

    @Query("SELECT DISTINCT status FROM manga ORDER BY status ASC")
    suspend fun getAllStatuses(): List<String>
}
