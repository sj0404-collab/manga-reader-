package com.mytech.mangatalkreader.domain.model

import com.mytech.mangatalkreader.data.db.entity.MangaEntity

data class Manga(
    val id: Long = 0,
    val sourceId: Long,
    val sourceMangaId: String,
    val title: String,
    val description: String = "",
    val coverUrl: String = "",
    val author: String = "",
    val artist: String = "",
    val status: MangaStatus = MangaStatus.UNKNOWN,
    val genres: List<String> = emptyList(),
    val rating: Float = 0f,
    val lastReadChapterId: Long? = null,
    val lastReadPage: Int = 0,
    val lastReadDate: Long? = null,
    val addedDate: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val chapterCount: Int = 0,
    val isFavorite: Boolean = false,
    val localCoverPath: String? = null
) {
    fun toEntity(): MangaEntity {
        return MangaEntity(
            id = id,
            sourceId = sourceId,
            sourceMangaId = sourceMangaId,
            title = title,
            description = description,
            coverUrl = coverUrl,
            author = author,
            artist = artist,
            status = status.value,
            genre = genres.joinToString(","),
            rating = rating,
            lastReadChapterId = lastReadChapterId,
            lastReadPage = lastReadPage,
            lastReadDate = lastReadDate,
            addedDate = addedDate,
            lastUpdated = lastUpdated,
            chapterCount = chapterCount,
            isFavorite = isFavorite,
            localCoverPath = localCoverPath
        )
    }

    companion object {
        fun fromEntity(entity: MangaEntity): Manga {
            return Manga(
                id = entity.id,
                sourceId = entity.sourceId,
                sourceMangaId = entity.sourceMangaId,
                title = entity.title,
                description = entity.description,
                coverUrl = entity.coverUrl,
                author = entity.author,
                artist = entity.artist,
                status = MangaStatus.fromValue(entity.status),
                genres = if (entity.genre.isBlank()) emptyList() else entity.genre.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                rating = entity.rating,
                lastReadChapterId = entity.lastReadChapterId,
                lastReadPage = entity.lastReadPage,
                lastReadDate = entity.lastReadDate,
                addedDate = entity.addedDate,
                lastUpdated = entity.lastUpdated,
                chapterCount = entity.chapterCount,
                isFavorite = entity.isFavorite,
                localCoverPath = entity.localCoverPath
            )
        }
    }
}

enum class MangaStatus(val value: String) {
    ONGOING("ongoing"),
    COMPLETED("completed"),
    HIATUS("hiatus"),
    CANCELLED("cancelled"),
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String): MangaStatus {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}
