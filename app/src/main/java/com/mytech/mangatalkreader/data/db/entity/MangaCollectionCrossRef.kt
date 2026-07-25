package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "manga_collection_cross_ref",
    primaryKeys = ["manga_id", "collection_id"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["collection_id"])
    ]
)
data class MangaCollectionCrossRef(
    @ColumnInfo(name = "manga_id")
    val mangaId: Long,

    @ColumnInfo(name = "collection_id")
    val collectionId: Long,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
)
