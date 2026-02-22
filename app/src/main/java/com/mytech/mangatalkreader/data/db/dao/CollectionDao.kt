package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import com.mytech.mangatalkreader.data.model.CollectionWithManga
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Insert
    suspend fun insertMangaToCollection(crossRef: MangaCollectionCrossRef)

    @Delete
    suspend fun removeMangaFromCollection(crossRef: MangaCollectionCrossRef)

    @Query("SELECT * FROM collection ORDER BY dateCreated DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collection WHERE id = :id")
    suspend fun getCollectionById(id: Long): CollectionEntity?

    @Query("DELETE FROM collection WHERE id = :id")
    suspend fun deleteCollectionById(id: Long)

    @Query("SELECT * FROM manga_collection_cross WHERE mangaId = :mangaId")
    suspend fun getCollectionsForManga(mangaId: Long): List<MangaCollectionCrossRef>

    @Transaction
    @Query("SELECT * FROM collection WHERE id = :collectionId")
    fun getCollectionWithManga(collectionId: Long): Flow<CollectionWithManga?>
}
