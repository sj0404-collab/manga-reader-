package com.mytech.mangatalkreader.domain.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsService @Inject constructor(
    @ApplicationContext private val context: Context
) : OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val isInitialized = MutableStateFlow(false)
    val isInitializedFlow: StateFlow<Boolean> = isInitialized

    private var isReady = false

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            isInitialized.value = true
            textToSpeech.language = Locale("ru")
            Timber.d("TTS initialized successfully")
        } else {
            Timber.e("TTS initialization failed")
            isInitialized.value = false
        }
    }

    fun speak(text: String, language: String = "ru", speed: Float = 1f, pitch: Float = 1f) {
        if (!isReady) {
            Timber.w("TTS not ready yet")
            return
        }

        try {
            val locale = when (language) {
                "ja" -> Locale.JAPANESE
                "en" -> Locale.ENGLISH
                "ru" -> Locale("ru")
                else -> Locale("ru")
            }

            textToSpeech.language = locale
            textToSpeech.setSpeechRate(speed)
            textToSpeech.setPitch(pitch)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        } catch (e: Exception) {
            Timber.e(e, "Error speaking text")
        }
    }

    fun stop() {
        if (isReady && textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    fun shutdown() {
        if (isReady) {
            textToSpeech.shutdown()
        }
    }

    fun isSpeaking(): Boolean = isReady && textToSpeech.isSpeaking
}
