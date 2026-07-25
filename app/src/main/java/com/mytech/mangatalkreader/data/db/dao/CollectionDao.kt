package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collections: List<CollectionEntity>): List<Long>

    @Update
    suspend fun update(collection: CollectionEntity): Int

    @Delete
    suspend fun delete(collection: CollectionEntity): Int

    @Query("DELETE FROM collection WHERE id = :collectionId AND is_system = 0")
    suspend fun deleteById(collectionId: Long): Int

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    suspend fun getCollectionById(collectionId: Long): CollectionEntity?

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    fun getCollectionByIdAsFlow(collectionId: Long): Flow<CollectionEntity?>

    @Query("SELECT * FROM collection ORDER BY sort_order ASC, name ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collection WHERE is_system = 1 ORDER BY sort_order ASC, name ASC")
    fun getSystemCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collection WHERE is_system = 0 ORDER BY sort_order ASC, name ASC")
    fun getUserCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collection WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCollections(query: String): Flow<List<CollectionEntity>>

    @Query("SELECT COUNT(*) FROM collection")
    fun getCollectionCount(): Flow<Int>

    @Query("UPDATE collection SET sort_order = :order WHERE id = :collectionId")
    suspend fun updateSortOrder(collectionId: Long, order: Int): Int

    @Query("UPDATE collection SET manga_count = :count WHERE id = :collectionId")
    suspend fun updateMangaCount(collectionId: Long, count: Int): Int

    @Query("SELECT * FROM collection ORDER BY sort_order ASC LIMIT 1")
    suspend fun getFirstCollection(): CollectionEntity?

    @Query(
        """
        SELECT c.* FROM collection c
        INNER JOIN manga_collection_cross_ref mcr ON c.id = mcr.collection_id
        WHERE mcr.manga_id = :mangaId
        ORDER BY c.sort_order ASC, c.name ASC
        """
    )
    fun getCollectionsForManga(mangaId: Long): Flow<List<CollectionEntity>>

    @Query(
        """
        SELECT mcr.manga_id FROM manga_collection_cross_ref mcr
        WHERE mcr.collection_id = :collectionId
        """
    )
    fun getMangaIdsInCollection(collectionId: Long): Flow<List<Long>>

    @Query(
        """
        SELECT COUNT(*) FROM manga_collection_cross_ref
        WHERE collection_id = :collectionId
        """
    )
    fun getMangaCountInCollection(collectionId: Long): Flow<Int>

    @Transaction
    suspend fun insertAndGetId(collection: CollectionEntity): Long {
        return insert(collection)
    }
}
