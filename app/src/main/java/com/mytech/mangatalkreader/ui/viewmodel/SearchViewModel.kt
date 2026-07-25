package com.mytech.mangatalkreader.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.data.source.model.MangaInfo
import com.mytech.mangatalkreader.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<MangaInfo>>(emptyList())
        private set

    var isSearching by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        searchQuery = query
        performSearchDebounced(query)
    }

    private fun performSearchDebounced(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            searchResults = emptyList()
            errorMessage = null
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            isSearching = true
            errorMessage = null
            try {
                val sourceIds = sourceRepository.getAllSourceIds()
                val allResults = mutableListOf<MangaInfo>()
                for (sourceId in sourceIds) {
                    try {
                        val results = sourceRepository.searchManga(sourceId, query, page = 1)
                        allResults.addAll(results)
                    } catch (e: Exception) {
                        // Continue with other sources if one fails
                    }
                }
                searchResults = allResults
            } catch (e: Exception) {
                errorMessage = e.message ?: "Search failed"
                searchResults = emptyList()
            } finally {
                isSearching = false
            }
        }
    }
}
