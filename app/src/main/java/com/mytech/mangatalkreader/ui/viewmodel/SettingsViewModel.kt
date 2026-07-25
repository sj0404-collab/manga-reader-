package com.mytech.mangatalkreader.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mytech.mangatalkreader.service.OcrLanguage
import com.mytech.mangatalkreader.service.OcrService
import com.mytech.mangatalkreader.service.TtsService
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val ocrService: OcrService,
    val ttsService: TtsService
) : ViewModel() {

    var isOcrEnabled by mutableStateOf(true)
        private set

    var isTtsEnabled by mutableStateOf(true)
        private set

    var selectedOcrLanguage by mutableStateOf(OcrLanguage.ENGLISH)
        private set

    var speechRate by mutableFloatStateOf(1.0f)
        private set

    var pitch by mutableFloatStateOf(1.0f)
        private set

    val availableOcrLanguages = OcrLanguage.entries.toList()

    val availableTtsLanguages: List<Locale>
        get() = ttsService.getAvailableLanguages().toList()

    fun toggleOcr() {
        isOcrEnabled = !isOcrEnabled
    }

    fun toggleTts() {
        isTtsEnabled = !isTtsEnabled
    }

    fun setOcrLanguage(language: OcrLanguage) {
        selectedOcrLanguage = language
        ocrService.setLanguage(language)
    }

    fun setSpeechRate(rate: Float) {
        speechRate = rate.coerceIn(0.1f, 3.0f)
        ttsService.setSpeechRate(speechRate)
    }

    fun setPitch(pitch: Float) {
        this.pitch = pitch.coerceIn(0.1f, 2.0f)
        ttsService.setPitch(this.pitch)
    }

    fun previewSpeech() {
        ttsService.speak("This is a test of the text to speech system.")
    }

    fun setTtsLanguage(locale: Locale) {
        ttsService.setLanguage(locale)
    }

    override fun onCleared() {
        super.onCleared()
        ocrService.close()
        ttsService.shutdown()
    }
}
