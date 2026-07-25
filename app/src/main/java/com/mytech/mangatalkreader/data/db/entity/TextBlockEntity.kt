package com.mytech.mangatalkreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "text_block",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chapter_id"]),
        Index(value = ["chapter_id", "page_number"])
    ]
)
data class TextBlockEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long,

    @ColumnInfo(name = "page_number")
    val pageNumber: Int,

    @ColumnInfo(name = "block_index")
    val blockIndex: Int,

    @ColumnInfo(name = "bounding_box_left")
    val boundingBoxLeft: Float,

    @ColumnInfo(name = "bounding_box_top")
    val boundingBoxTop: Float,

    @ColumnInfo(name = "bounding_box_right")
    val boundingBoxRight: Float,

    @ColumnInfo(name = "bounding_box_bottom")
    val boundingBoxBottom: Float,

    @ColumnInfo(name = "original_text")
    val originalText: String = "",

    @ColumnInfo(name = "translated_text")
    val translatedText: String = "",

    @ColumnInfo(name = "source_language")
    val sourceLanguage: String = "ja", // ja, zh, ko, en, etc.

    @ColumnInfo(name = "target_language")
    val targetLanguage: String = "en",

    @ColumnInfo(name = "confidence")
    val confidence: Float = 0f,

    @ColumnInfo(name = "is_edited")
    val isEdited: Boolean = false,

    @ColumnInfo(name = "is_translated")
    val isTranslated: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
