package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manga_id"]),
        Index(value = ["manga_id", "chapter_number"], unique = true)
    ]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "manga_id")
    val mangaId: Long,

    @ColumnInfo(name = "source_chapter_id")
    val sourceChapterId: String,

    @ColumnInfo(name = "chapter_number")
    val chapterNumber: Float,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "page_count")
    val pageCount: Int = 0,

    @ColumnInfo(name = "pages_json")
    val pagesJson: String = "[]", // JSON array of page URLs

    @ColumnInfo(name = "is_downloaded")
    val isDownloaded: Boolean = false,

    @ColumnInfo(name = "download_path")
    val downloadPath: String? = null,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "last_read_page")
    val lastReadPage: Int = 0,

    @ColumnInfo(name = "fetched_date")
    val fetchedDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "upload_date")
    val uploadDate: Long? = null,

    @ColumnInfo(name = "scanlator")
    val scanlator: String = ""
)
