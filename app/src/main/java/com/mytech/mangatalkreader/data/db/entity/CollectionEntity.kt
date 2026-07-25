package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "icon")
    val icon: String = "", // icon identifier or emoji

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "is_system")
    val isSystem: Boolean = false, // system collections like "Reading", "Completed" cannot be deleted

    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "manga_count")
    val mangaCount: Int = 0
)
