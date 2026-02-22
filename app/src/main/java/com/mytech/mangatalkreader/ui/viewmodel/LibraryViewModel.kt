package com.mytech.mangatalkreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.data.model.Manga
import com.mytech.mangatalkreader.data.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _mangaList = MutableStateFlow<List<Manga>>(emptyList())
    val mangaList: StateFlow<List<Manga>> = _mangaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadManga()
    }

    private fun loadManga() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                mangaRepository.getAllManga().collect { mangas ->
                    _mangaList.value = mangas
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading manga")
                _isLoading.value = false
            }
        }
    }

    fun searchManga(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            loadManga()
        } else {
            viewModelScope.launch {
                try {
                    mangaRepository.searchManga(query).collect { mangas ->
                        _mangaList.value = mangas
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error searching manga")
                }
            }
        }
    }

    fun deleteManga(mangaId: Long) {
        viewModelScope.launch {
            try {
                mangaRepository.deleteManga(mangaId)
                loadManga()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting manga")
            }
        }
    }

    fun toggleFavorite(mangaId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                mangaRepository.toggleFavorite(mangaId, isFavorite)
                loadManga()
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
            }
        }
    }

    fun refreshManga() {
        loadManga()
    }
}
