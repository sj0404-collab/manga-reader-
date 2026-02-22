package com.mytech.mangatalkreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.domain.service.OcrService
import com.mytech.mangatalkreader.domain.service.TtsService
import com.mytech.mangatalkreader.data.model.TextBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val ocrService: OcrService,
    private val ttsService: TtsService
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(0)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _textBlocks = MutableStateFlow<List<TextBlock>>(emptyList())
    val textBlocks: StateFlow<List<TextBlock>> = _textBlocks.asStateFlow()

    private val _isOcrProcessing = MutableStateFlow(false)
    val isOcrProcessing: StateFlow<Boolean> = _isOcrProcessing.asStateFlow()

    private val _readingMode = MutableStateFlow("right_to_left")
    val readingMode: StateFlow<String> = _readingMode.asStateFlow()

    private val _showPageNumber = MutableStateFlow(true)
    val showPageNumber: StateFlow<Boolean> = _showPageNumber.asStateFlow()

    fun setTotalPages(total: Int) {
        _totalPages.value = total
    }

    fun nextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value++
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun goToPage(page: Int) {
        if (page in 0 until _totalPages.value) {
            _currentPage.value = page
        }
    }

    fun setReadingMode(mode: String) {
        _readingMode.value = mode
    }

    fun setShowPageNumber(show: Boolean) {
        _showPageNumber.value = show
    }

    fun speakText(text: String, language: String = "ru") {
        ttsService.speak(text, language)
    }

    fun stopSpeaking() {
        ttsService.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.shutdown()
    }
}
