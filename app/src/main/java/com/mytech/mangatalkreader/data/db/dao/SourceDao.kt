package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: SourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<SourceEntity>): List<Long>

    @Update
    suspend fun update(source: SourceEntity): Int

    @Delete
    suspend fun delete(source: SourceEntity): Int

    @Query("DELETE FROM source WHERE id = :sourceId")
    suspend fun deleteById(sourceId: Long): Int

    @Query("SELECT * FROM source WHERE id = :sourceId")
    suspend fun getSourceById(sourceId: Long): SourceEntity?

    @Query("SELECT * FROM source WHERE id = :sourceId")
    fun getSourceByIdAsFlow(sourceId: Long): Flow<SourceEntity?>

    @Query("SELECT * FROM source WHERE base_url = :baseUrl LIMIT 1")
    suspend fun getSourceByBaseUrl(baseUrl: String): SourceEntity?

    @Query("SELECT * FROM source WHERE is_active = 1 ORDER BY name ASC")
    fun getActiveSources(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM source ORDER BY name ASC")
    fun getAllSources(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM source WHERE language = :language AND is_active = 1 ORDER BY name ASC")
    fun getSourcesByLanguage(language: String): Flow<List<SourceEntity>>

    @Query("SELECT * FROM source WHERE is_nsfw = 0 AND is_active = 1 ORDER BY name ASC")
    fun getSfwSources(): Flow<List<SourceEntity>>

    @Query("SELECT COUNT(*) FROM source")
    fun getSourceCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM source WHERE is_active = 1")
    fun getActiveSourceCount(): Flow<Int>

    @Query("UPDATE source SET is_active = :isActive WHERE id = :sourceId")
    suspend fun setActiveStatus(sourceId: Long, isActive: Boolean): Int

    @Query("UPDATE source SET last_used_date = :lastUsedDate WHERE id = :sourceId")
    suspend fun updateLastUsed(sourceId: Long, lastUsedDate: Long = System.currentTimeMillis()): Int

    @Query("UPDATE source SET manga_count = :count WHERE id = :sourceId")
    suspend fun updateMangaCount(sourceId: Long, count: Int): Int

    @Query("SELECT * FROM source WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchSources(query: String): Flow<List<SourceEntity>>

    @Query("SELECT DISTINCT language FROM source WHERE is_active = 1 ORDER BY language ASC")
    suspend fun getAvailableLanguages(): List<String>
}
