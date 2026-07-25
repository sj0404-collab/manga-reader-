package com.mytech.mangatalkreader.data.source

import com.mytech.mangatalkreader.data.source.model.ChapterInfo
import com.mytech.mangatalkreader.data.source.model.MangaDetailInfo
import com.mytech.mangatalkreader.data.source.model.MangaInfo

interface MangaSource {
    val id: String
    val name: String
    val baseUrl: String

    suspend fun getPopularManga(page: Int): List<MangaInfo>

    suspend fun searchManga(query: String, page: Int): List<MangaInfo>

    suspend fun getMangaDetails(mangaId: String): MangaDetailInfo

    suspend fun getChapterList(mangaId: String): List<ChapterInfo>

    suspend fun getPageList(chapterId: String): List<String>
}
