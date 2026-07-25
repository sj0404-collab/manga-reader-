package com.mytech.mangatalkreader.data.source.model

data class MangaInfo(
    val id: String,
    val title: String,
    val coverUrl: String,
    val description: String = "",
    val author: String = "",
    val status: String = "",
    val genres: List<String> = emptyList(),
    val rating: Float = 0f,
    val year: Int? = null
)

data class MangaDetailInfo(
    val id: String,
    val title: String,
    val altTitles: List<String> = emptyList(),
    val coverUrl: String,
    val description: String = "",
    val author: String = "",
    val artist: String = "",
    val status: String = "",
    val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val rating: Float = 0f,
    val year: Int? = null,
    val chapterCount: Int = 0,
    val lastUpdated: Long = 0L
)

data class ChapterInfo(
    val id: String,
    val mangaId: String,
    val chapterNumber: Float,
    val title: String = "",
    val scanlator: String = "",
    val language: String = "en",
    val uploadDate: Long = 0L,
    val pageCount: Int = 0,
    val volume: String? = null
)
