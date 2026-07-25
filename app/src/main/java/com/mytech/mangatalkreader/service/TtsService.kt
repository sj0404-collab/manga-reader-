package com.mytech.mangatalkreader.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Events emitted by [TtsService] during its lifecycle.
 */
sealed class TtsEvent {
    data object Ready : TtsEvent()
    data object SpeakingStarted : TtsEvent()
    data object SpeakingStopped : TtsEvent()
    data class Error(val message: String) : TtsEvent()
    data class UtteranceCompleted(val utteranceId: String) : TtsEvent()
}

/**
 * Android TextToSpeech wrapper that handles the full lifecycle properly.
 *
 * - Implements [TextToSpeech.OnInitListener] for initialization callbacks.
 * - Works with whatever TTS engine is installed on the device
 *   (Google TTS online, Pico offline, Samsung TTS, etc.).
 * - Exposes a [events] SharedFlow so consumers can react to TTS state changes.
 * - Provides [speak], [stop], [setLanguage], [setSpeechRate], and [setPitch].
 *
 * Usage (Hilt-injected):
 *   ttsService.speak("Hello world")
 *   ttsService.setLanguage(Locale.JAPAN)
 *   ttsService.stop()
 */
@Singleton
class TtsService @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var isInitSucceeded = false

    private val _events = MutableSharedFlow<TtsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<TtsEvent> = _events.asSharedFlow()

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    // --------------- TextToSpeech.OnInitListener ---------------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech ?: return
            isInitSucceeded = true

            // Set a default language (US English)
            val defaultLocale = Locale.US
            val result = tts.language = defaultLocale
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                emitEvent(TtsEvent.Error("Default language $defaultLocale is not available"))
            }

            // Attach utterance progress listener
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    emitEvent(TtsEvent.SpeakingStarted)
                }

                override fun onDone(utteranceId: String) {
                    emitEvent(TtsEvent.UtteranceCompleted(utteranceId))
                    emitEvent(TtsEvent.SpeakingStopped)
                }

                @Deprecated("Deprecated but still called on older API levels")
                override fun onError(utteranceId: String) {
                    emitEvent(TtsEvent.Error("TTS error on utterance $utteranceId"))
                    emitEvent(TtsEvent.SpeakingStopped)
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    emitEvent(TtsEvent.Error("TTS error $errorCode on utterance $utteranceId"))
                    emitEvent(TtsEvent.SpeakingStopped)
                }
            })

            isInitialized = true
            emitEvent(TtsEvent.Ready)
        } else {
            isInitSucceeded = false
            emitEvent(TtsEvent.Error("TextToSpeech initialization failed with status=$status"))
        }
    }

    // --------------- Public API ---------------

    /**
     * Speak the given [text] synchronously (blocks until finished).
     * Uses the current language, speech rate, and pitch settings.
     *
     * If TTS is not yet initialized this call returns immediately with no effect.
     */
    fun speak(text: String) {
        val tts = textToSpeech
        if (!isInitialized || tts == null) {
            emitEvent(TtsEvent.Error("TTS not initialized"))
            return
        }

        // Generate a unique utterance ID so we can track completion
        val utteranceId = "utterance_${System.currentTimeMillis()}"

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    /**
     * Speak the given [text] asynchronously without blocking the caller.
     * This uses [TextToSpeech.QUEUE_ADD] so utterances are queued.
     */
    fun speakAsync(text: String) {
        val tts = textToSpeech
        if (!isInitialized || tts == null) {
            emitEvent(TtsEvent.Error("TTS not initialized"))
            return
        }

        val utteranceId = "utterance_${System.currentTimeMillis()}"
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
    }

    /**
     * Stop the current utterance and discard any queued utterances.
     */
    fun stop() {
        textToSpeech?.stop()
    }

    /**
     * Check whether the TTS engine is currently speaking.
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    /**
     * Set the TTS language.
     *
     * Returns [TextToSpeech.LANG_AVAILABLE], [TextToSpeech.LANG_COUNTRY_AVAILABLE],
     * [TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE], or one of the error codes
     * [TextToSpeech.LANG_MISSING_DATA] / [TextToSpeech.LANG_NOT_SUPPORTED].
     */
    fun setLanguage(locale: Locale): Int {
        val tts = textToSpeech
        if (!isInitialized || tts == null) {
            emitEvent(TtsEvent.Error("TTS not initialized"))
            return TextToSpeech.ERROR
        }
        return tts.setLanguage(locale)
    }

    /**
     * Set the speech rate.
     *
     * @param rate 1.0 is the normal rate. Values &lt; 1.0 slow down speech;
     *             values &gt; 1.0 speed it up. Range is roughly 0.1 to 3.0.
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }

    /**
     * Set the pitch.
     *
     * @param pitch 1.0 is the normal pitch. Values &lt; 1.0 lower the pitch;
     *              values &gt; 1.0 raise it. Range is roughly 0.1 to 2.0.
     */
    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch)
    }

    /**
     * Return the list of locales that the current TTS engine supports.
     * Requires API 21+.
     */
    fun getAvailableLanguages(): Set<Locale> {
        val tts = textToSpeech
        if (!isInitialized || tts == null) return emptySet()
        return try {
            tts.availableLanguages
        } catch (_: UnsupportedOperationException) {
            emptySet()
        }
    }

    /**
     * Return the default engine for the given [locale].
     */
    fun getDefaultEngine(): String? {
        return textToSpeech?.defaultEngine
    }

    // --------------- Internal ---------------

    private fun emitEvent(event: TtsEvent) {
        // tryEmit so we never suspend; events flow is unbounded.
        _events.tryEmit(event)
    }

    /**
     * Shut down the TextToSpeech engine and release all resources.
     * After calling this method the service cannot be reused.
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
        isInitSucceeded = false
    }
}
