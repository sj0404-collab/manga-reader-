package com.mytech.mangatalkreader.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import java.util.Date

data class CollectionWithManga(
    @Embedded
    val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        childColumn = "mangaId",
        associateBy = Junction(MangaCollectionCrossRef::class)
    )
    val mangas: List<MangaEntity>
)

data class TTSProfile(
    val id: Long = 0,
    val name: String,
    val speed: Float = 1f,
    val pitch: Float = 1f,
    val volume: Float = 1f,
    val language: String = "ru",
    val textType: String = "dialog"
)

data class OCRSettings(
    val languages: List<String> = listOf("ru", "ja", "en"),
    val priorityLanguage: String = "ru",
    val autoOCR: Boolean = true,
    val sensitivity: Float = 0.5f,
    val minTextSize: Int = 12,
    val boundingBoxMode: String = "precise",
    val ignoreNonTargetSymbols: Boolean = true,
    val highlightBalloons: Boolean = true
)

data class ReaderSettings(
    val readingMode: String = "right_to_left",
    val showPageNumber: Boolean = true,
    val tapNavigationEnabled: Boolean = true,
    val keepScreenOn: Boolean = false,
    val fitToWidth: Boolean = true,
    val cropBorders: Boolean = false
)

data class TextBlock(
    val id: Long = 0,
    val chapterId: Long,
    val pageNumber: Int,
    val text: String,
    val type: String,
    val language: String = "ru",
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val textColor: Int = -1,
    val backgroundColor: Int? = null,
    val fontSize: Float = 16f,
    val isManual: Boolean = false,
    val hasAudio: Boolean = false,
    val audioPath: String? = null,
    val ttsSpeed: Float = 1f,
    val ttsPitch: Float = 1f,
    val dateCreated: Date = Date()
)

data class Manga(
    val id: Long = 0,
    val title: String,
    val coverPath: String? = null,
    val description: String? = null,
    val source: String? = null,
    val sourceUrl: String? = null,
    val currentChapter: Int = 0,
    val totalChapters: Int = 0,
    val progress: Float = 0f,
    val isRead: Boolean = false,
    val dateAdded: Date = Date(),
    val lastReadDate: Date? = null,
    val isFavorite: Boolean = false
)

data class Chapter(
    val id: Long = 0,
    val mangaId: Long,
    val title: String,
    val number: Int,
    val path: String? = null,
    val url: String? = null,
    val pageCount: Int = 0,
    val currentPage: Int = 0,
    val isRead: Boolean = false,
    val dateRead: Date? = null
)

data class Collection(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val iconColor: Int = -1,
    val dateCreated: Date = Date()
)

data class Source(
    val id: Long = 0,
    val name: String,
    val url: String,
    val iconUrl: String? = null,
    val isActive: Boolean = true,
    val dateAdded: Date = Date(),
    val lastChecked: Date? = null,
    val updateCheckInterval: Long = 86400000
)
