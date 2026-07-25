package com.mytech.mangatalkreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val chapterDao: ChapterDao
) : ViewModel() {

    private val _pageUrls = MutableStateFlow<List<String>>(emptyList())
    val pageUrls: StateFlow<List<String>> = _pageUrls.asStateFlow()

    private val _chapter = MutableStateFlow<ChapterEntity?>(null)
    val chapter: StateFlow<ChapterEntity?> = _chapter.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun loadChapter(chapterId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterDao.getChapterByIdAsFlow(chapterId).collect { ch ->
                _chapter.value = ch
                // Load page URLs from chapter's pagesJson
                if (ch != null && ch.pagesJson.isNotEmpty()) {
                    try {
                        val gson = com.google.gson.Gson()
                        val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
                        val urls = gson.fromJson<List<String>>(ch.pagesJson, type) ?: emptyList()
                        _pageUrls.value = urls
                    } catch (e: Exception) {
                        _pageUrls.value = emptyList()
                    }
                }
            }
        }
    }

    fun setPageUrls(urls: List<String>) {
        _pageUrls.value = urls
    }

    fun saveProgress(pageIndex: Int) {
        _currentPage.value = pageIndex
        viewModelScope.launch(Dispatchers.IO) {
            _chapter.value?.let { ch ->
                chapterDao.updateReadProgress(ch.id, pageIndex)
            }
        }
    }
}
