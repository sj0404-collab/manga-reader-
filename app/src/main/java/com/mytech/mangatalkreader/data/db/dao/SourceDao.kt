package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Insert
    suspend fun insert(source: SourceEntity): Long

    @Update
    suspend fun update(source: SourceEntity)

    @Delete
    suspend fun delete(source: SourceEntity)

    @Query("SELECT * FROM source WHERE id = :id")
    suspend fun getSourceById(id: Long): SourceEntity?

    @Query("SELECT * FROM source WHERE isActive = 1 ORDER BY dateAdded DESC")
    fun getActiveSources(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM source ORDER BY dateAdded DESC")
    fun getAllSources(): Flow<List<SourceEntity>>

    @Query("UPDATE source SET isActive = :isActive WHERE id = :id")
    suspend fun updateActive(id: Long, isActive: Boolean)

    @Query("DELETE FROM source WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE source SET lastChecked = datetime('now') WHERE id = :id")
    suspend fun updateLastChecked(id: Long)
}
