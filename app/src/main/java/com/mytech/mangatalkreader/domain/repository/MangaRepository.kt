package com.mytech.mangatalkreader.domain.repository

import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import kotlinx.coroutines.flow.Flow

interface MangaRepository {
    fun getAllMangaByLastRead(): Flow<List<MangaEntity>>
    fun getAllMangaByTitle(): Flow<List<MangaEntity>>
    fun getAllMangaByDateAdded(): Flow<List<MangaEntity>>
    fun getFavoriteManga(): Flow<List<MangaEntity>>
    fun getRecentlyReadManga(limit: Int = 10): Flow<List<MangaEntity>>
    fun getMangaBySourceId(sourceId: Long): Flow<List<MangaEntity>>
    fun getMangaByIdAsFlow(mangaId: Long): Flow<MangaEntity?>
    fun searchManga(query: String): Flow<List<MangaEntity>>
    fun getMangaCount(): Flow<Int>
    fun getFavoriteCount(): Flow<Int>

    suspend fun insert(manga: MangaEntity): Long
    suspend fun insertAll(mangaList: List<MangaEntity>): List<Long>
    suspend fun update(manga: MangaEntity): Int
    suspend fun delete(manga: MangaEntity): Int
    suspend fun deleteById(mangaId: Long): Int
    suspend fun getMangaById(mangaId: Long): MangaEntity?
    suspend fun getMangaBySourceId(sourceMangaId: String, sourceId: Long): MangaEntity?
    suspend fun setFavorite(mangaId: Long, isFavorite: Boolean): Int
    suspend fun updateReadProgress(mangaId: Long, chapterId: Long, page: Int, readDate: Long): Int
}
