package com.mytech.mangatalkreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mangaDao: MangaDao
) : ViewModel() {

    val mangaList = mangaDao.getAllManga()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToLibrary(manga: MangaEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mangaDao.insert(manga)
        }
    }

    fun removeFromLibrary(mangaId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mangaDao.deleteById(mangaId)
        }
    }

    fun toggleFavorite(mangaId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val manga = mangaDao.getMangaByIdAsFlow(mangaId).firstOrNull()
            if (manga != null) {
                mangaDao.setFavorite(mangaId, !manga.isFavorite)
            }
        }
    }
}
