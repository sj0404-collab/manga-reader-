package com.mytech.mangatalkreader.domain.repository

import com.mytech.mangatalkreader.data.source.model.ChapterInfo
import com.mytech.mangatalkreader.data.source.model.MangaDetailInfo
import com.mytech.mangatalkreader.data.source.model.MangaInfo
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    fun getAllSourceIds(): List<String>

    suspend fun getPopularManga(sourceId: String, page: Int): List<MangaInfo>
    suspend fun searchManga(sourceId: String, query: String, page: Int): List<MangaInfo>
    suspend fun getMangaDetails(sourceId: String, mangaId: String): MangaDetailInfo
    suspend fun getChapterList(sourceId: String, mangaId: String): List<ChapterInfo>
    suspend fun getPageList(sourceId: String, chapterId: String): List<String>
}
