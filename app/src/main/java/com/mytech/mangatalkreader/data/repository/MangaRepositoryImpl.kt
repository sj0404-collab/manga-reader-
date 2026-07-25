package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import com.mytech.mangatalkreader.domain.repository.MangaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao,
    private val applicationScope: CoroutineScope,
    @com.mytech.mangatalkreader.di.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MangaRepository {

    override fun getAllMangaByLastRead(): Flow<List<MangaEntity>> =
        mangaDao.getAllMangaByLastRead().flowOn(ioDispatcher)

    override fun getAllMangaByTitle(): Flow<List<MangaEntity>> =
        mangaDao.getAllMangaByTitle().flowOn(ioDispatcher)

    override fun getAllMangaByDateAdded(): Flow<List<MangaEntity>> =
        mangaDao.getAllMangaByDateAdded().flowOn(ioDispatcher)

    override fun getFavoriteManga(): Flow<List<MangaEntity>> =
        mangaDao.getFavoriteManga().flowOn(ioDispatcher)

    override fun getRecentlyReadManga(limit: Int): Flow<List<MangaEntity>> =
        mangaDao.getRecentlyReadManga(limit).flowOn(ioDispatcher)

    override fun getMangaBySourceId(sourceId: Long): Flow<List<MangaEntity>> =
        mangaDao.getMangaBySourceId(sourceId).flowOn(ioDispatcher)

    override fun getMangaByIdAsFlow(mangaId: Long): Flow<MangaEntity?> =
        mangaDao.getMangaByIdAsFlow(mangaId).flowOn(ioDispatcher)

    override fun searchManga(query: String): Flow<List<MangaEntity>> =
        mangaDao.searchManga(query).flowOn(ioDispatcher)

    override fun getMangaCount(): Flow<Int> =
        mangaDao.getMangaCount().flowOn(ioDispatcher)

    override fun getFavoriteCount(): Flow<Int> =
        mangaDao.getFavoriteCount().flowOn(ioDispatcher)

    override suspend fun insert(manga: MangaEntity): Long =
        withContext(ioDispatcher) { mangaDao.insert(manga) }

    override suspend fun insertAll(mangaList: List<MangaEntity>): List<Long> =
        withContext(ioDispatcher) { mangaDao.insertAll(mangaList) }

    override suspend fun update(manga: MangaEntity): Int =
        withContext(ioDispatcher) { mangaDao.update(manga) }

    override suspend fun delete(manga: MangaEntity): Int =
        withContext(ioDispatcher) { mangaDao.delete(manga) }

    override suspend fun deleteById(mangaId: Long): Int =
        withContext(ioDispatcher) { mangaDao.deleteById(mangaId) }

    override suspend fun getMangaById(mangaId: Long): MangaEntity? =
        withContext(ioDispatcher) { mangaDao.getMangaById(mangaId) }

    override suspend fun getMangaBySourceId(sourceMangaId: String, sourceId: Long): MangaEntity? =
        withContext(ioDispatcher) { mangaDao.getMangaBySourceId(sourceMangaId, sourceId) }

    override suspend fun setFavorite(mangaId: Long, isFavorite: Boolean): Int =
        withContext(ioDispatcher) { mangaDao.setFavorite(mangaId, isFavorite) }

    override suspend fun updateReadProgress(mangaId: Long, chapterId: Long, page: Int, readDate: Long): Int =
        withContext(ioDispatcher) { mangaDao.updateReadProgress(mangaId, chapterId, page, readDate) }
}
