package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "source")
data class SourceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "base_url")
    val baseUrl: String,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "language")
    val language: String = "en",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "is_nsfw")
    val isNsfw: Boolean = false,

    @ColumnInfo(name = "supports_search")
    val supportsSearch: Boolean = true,

    @ColumnInfo(name = "supports_latest")
    val supportsLatest: Boolean = true,

    @ColumnInfo(name = "supports_filters")
    val supportsFilters: Boolean = true,

    @ColumnInfo(name = "filter_json")
    val filterJson: String = "[]",

    @ColumnInfo(name = "headers_json")
    val headersJson: String = "{}",

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_used_date")
    val lastUsedDate: Long? = null,

    @ColumnInfo(name = "manga_count")
    val mangaCount: Int = 0
)
