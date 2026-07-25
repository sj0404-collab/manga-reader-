package com.mytech.mangatalkreader.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import com.mytech.mangatalkreader.domain.repository.ChapterRepository
import com.mytech.mangatalkreader.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mangaId: Long = savedStateHandle.get<Long>("mangaId") ?: 0L

    val manga: StateFlow<MangaEntity?> = mangaRepository
        .getMangaByIdAsFlow(mangaId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val chapters: StateFlow<List<ChapterEntity>> = chapterRepository
        .getChaptersByMangaIdAsc(mangaId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            manga.collect {
                isLoading = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentManga = manga.value ?: return@launch
            mangaRepository.setFavorite(currentManga.id, !currentManga.isFavorite)
        }
    }
}
