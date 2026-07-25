package com.mytech.mangatalkreader.data.source

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepository @Inject constructor(
    private val sources: List<MangaSource>
) {
    private val sourceMap: Map<String, MangaSource> = sources.associateBy { it.id }

    fun getAllSources(): List<MangaSource> = sources.toList()

    fun getSource(id: String): MangaSource? = sourceMap[id]
}
