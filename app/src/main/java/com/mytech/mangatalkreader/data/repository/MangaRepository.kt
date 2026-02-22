package com.mytech.mangatalkreader.data.repository

import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import com.mytech.mangatalkreader.data.model.Manga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepository @Inject constructor(
    private val mangaDao: MangaDao
) {
    fun getAllManga(): Flow<List<Manga>> = mangaDao.getAllManga().map { entities ->
        entities.map { it.toDomainModel() }
    }

    fun searchManga(query: String): Flow<List<Manga>> = mangaDao.searchManga(query).map { entities ->
        entities.map { it.toDomainModel() }
    }

    fun getFavoriteManga(): Flow<List<Manga>> = mangaDao.getFavoriteManga().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun getMangaById(id: Long): Manga? = mangaDao.getMangaById(id)?.toDomainModel()

    suspend fun addManga(manga: Manga): Long = mangaDao.insert(manga.toEntity())

    suspend fun updateManga(manga: Manga) = mangaDao.update(manga.toEntity())

    suspend fun deleteManga(id: Long) = mangaDao.deleteById(id)

    suspend fun updateProgress(id: Long, progress: Float) = mangaDao.updateProgress(id, progress)

    suspend fun updateReadStatus(id: Long, isRead: Boolean) = mangaDao.updateReadStatus(id, isRead)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = mangaDao.toggleFavorite(id, isFavorite)

    private fun MangaEntity.toDomainModel() = Manga(
        id = id,
        title = title,
        coverPath = coverPath,
        description = description,
        source = source,
        sourceUrl = sourceUrl,
        currentChapter = currentChapter,
        totalChapters = totalChapters,
        progress = progress,
        isRead = isRead,
        dateAdded = dateAdded,
        lastReadDate = lastReadDate,
        isFavorite = isFavorite
    )

    private fun Manga.toEntity() = MangaEntity(
        id = id,
        title = title,
        coverPath = coverPath,
        description = description,
        source = source,
        sourceUrl = sourceUrl,
        currentChapter = currentChapter,
        totalChapters = totalChapters,
        progress = progress,
        isRead = isRead,
        dateAdded = dateAdded,
        lastReadDate = lastReadDate,
        isFavorite = isFavorite
    )
}
