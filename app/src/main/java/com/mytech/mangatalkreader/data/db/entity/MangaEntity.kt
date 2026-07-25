package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "source_id")
    val sourceId: Long,

    @ColumnInfo(name = "source_manga_id")
    val sourceMangaId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "cover_url")
    val coverUrl: String = "",

    @ColumnInfo(name = "author")
    val author: String = "",

    @ColumnInfo(name = "artist")
    val artist: String = "",

    @ColumnInfo(name = "status")
    val status: String = "unknown", // ongoing, completed, hiatus, cancelled, unknown

    @ColumnInfo(name = "genre")
    val genre: String = "", // comma-separated genres

    @ColumnInfo(name = "rating")
    val rating: Float = 0f,

    @ColumnInfo(name = "last_read_chapter_id")
    val lastReadChapterId: Long? = null,

    @ColumnInfo(name = "last_read_page")
    val lastReadPage: Int = 0,

    @ColumnInfo(name = "last_read_date")
    val lastReadDate: Long? = null,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "chapter_count")
    val chapterCount: Int = 0,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "local_cover_path")
    val localCoverPath: String? = null
)
