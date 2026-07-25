package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.db.dao.CollectionDao
import com.mytech.mangatalkreader.data.db.dao.MangaCollectionCrossRefDao
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import com.mytech.mangatalkreader.domain.repository.CollectionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CollectionRepositoryImpl(
    private val collectionDao: CollectionDao,
    private val crossRefDao: MangaCollectionCrossRefDao,
    private val applicationScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) : CollectionRepository {

    override suspend fun insert(collection: CollectionEntity): Long =
        withContext(ioDispatcher) { collectionDao.insert(collection) }

    override suspend fun update(collection: CollectionEntity): Int =
        withContext(ioDispatcher) { collectionDao.update(collection) }

    override suspend fun delete(collection: CollectionEntity): Int =
        withContext(ioDispatcher) { collectionDao.delete(collection) }

    override suspend fun deleteById(id: Long): Int =
        withContext(ioDispatcher) { collectionDao.deleteById(id) }

    override suspend fun getById(id: Long): CollectionEntity? =
        withContext(ioDispatcher) { collectionDao.getCollectionById(id) }

    override fun getAllCollections(): Flow<List<CollectionEntity>> =
        collectionDao.getAllCollections()

    override fun getByIdAsFlow(id: Long): Flow<CollectionEntity?> =
        collectionDao.getCollectionByIdAsFlow(id)

    override suspend fun addMangaToCollection(crossRef: MangaCollectionCrossRef): Long =
        withContext(ioDispatcher) { crossRefDao.insert(crossRef) }

    override suspend fun removeMangaFromCollection(mangaId: Long, collectionId: Long): Int =
        withContext(ioDispatcher) { crossRefDao.deleteByMangaAndCollection(mangaId, collectionId) }

    override fun getMangaIdsForCollection(collectionId: Long): Flow<List<Long>> =
        collectionDao.getMangaIdsInCollection(collectionId)

    override suspend fun getCollectionIdsForManga(mangaId: Long): List<Long> =
        withContext(ioDispatcher) { crossRefDao.getCollectionIdsForManga(mangaId) }
}
