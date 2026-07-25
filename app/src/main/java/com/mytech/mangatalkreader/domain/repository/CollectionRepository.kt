package com.mytech.mangatalkreader.domain.repository

import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    suspend fun insert(collection: CollectionEntity): Long

    suspend fun update(collection: CollectionEntity): Int

    suspend fun delete(collection: CollectionEntity): Int

    suspend fun deleteById(id: Long): Int

    suspend fun getById(id: Long): CollectionEntity?

    fun getAllCollections(): Flow<List<CollectionEntity>>

    fun getByIdAsFlow(id: Long): Flow<CollectionEntity?>

    suspend fun addMangaToCollection(crossRef: MangaCollectionCrossRef): Long

    suspend fun removeMangaFromCollection(mangaId: Long, collectionId: Long): Int

    fun getMangaIdsForCollection(collectionId: Long): Flow<List<Long>>

    suspend fun getCollectionIdsForManga(mangaId: Long): List<Long>
}
