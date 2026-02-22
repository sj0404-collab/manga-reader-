package com.mytech.mangatalkreader.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mangaId")]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(
    tableName = "text_block",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chapterId")]
)
data class TextBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(tableName = "collection")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val iconColor: Int = -1,
    val dateCreated: Date = Date()
)

@Entity(
    tableName = "manga_collection_cross",
    primaryKeys = ["mangaId", "collectionId"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MangaCollectionCrossRef(
    val mangaId: Long,
    val collectionId: Long
)

@Entity(tableName = "source")
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val iconUrl: String? = null,
    val isActive: Boolean = true,
    val dateAdded: Date = Date(),
    val lastChecked: Date? = null,
    val updateCheckInterval: Long = 86400000
)
