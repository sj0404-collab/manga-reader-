package com.mytech.mangatalkreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytech.mangatalkreader.service.TtsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isFromUser: Boolean,
    val aiName: String? = null,
    val aiAvatarEmoji: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AiPersona(
    val displayName: String,
    val emoji: String,
    val personality: String,
    val language: String,
    val sampleResponses: Map<String, String>
) {
    MANGA_EXPERT(
        "Манга-Эксперт",
        "📚",
        "Знает всё о манге, аниме и японской культуре",
        "ru",
        mapOf(
            "манга" to "Я рекомендую начинать с классики — 'Akira', 'Ghost in the Shell', 'Berserk'. Для новичков отлично подходят 'One Piece' и 'My Hero Academia'!",
            "аниме" to "Если понравилась манга, обязательно посмотри аниме-адаптацию! Часто там добавляют музыку и динамику, которые усиливают эмоции.",
            "рекомендация" to "Попробуй 'Vinland Saga' — это историческая манга о викингах с потрясающим артом. Или 'Spy x Family' для лёгкого чтения!",
            "жанр" to "Самые популярные жанры: shonen (для парней), shojo (для девушек), seinen (для взрослых), isekai (другой мир). Что тебе ближе?",
            "читать" to "Мангу принято читать справа налево! Это японская традиция. Наш ридер поддерживает RTL-режим для удобства."
        )
    ),
    OCR_HELPER(
        "OCR-Помощник",
        "🔍",
        "Помогает с распознаванием текста и переводом",
        "ru",
        mapOf(
            "перевод" to "Я могу помочь перевести распознанный текст! Японский → русский или английский. Включи OCR в читалке и нажми кнопку перевода.",
            "японский" to "Для японского текста лучше всего использовать наш OCR с японским языком. Он распознает как кана (ひらがな/カタカナ), так и кандзи (漢字).",
            "текст" to "Если OCR плохо распознаёт — попробуй увеличить контраст в настройках Image Preprocessor. Или выбери другой язык распознавания.",
            "распознать" to "Наш OCR поддерживает 5 языков: английский, японский, китайский, корейский и деванагари. Переключайся в настройках!",
            "не работает" to "Проверь: 1) Язык OCR правильно выбран? 2) Картинка чёткая? 3) ML Kit модели загружены? Они скачиваются автоматически при первом использовании."
        )
    ),
    TTS_VOICE(
        "TTS-Голос",
        "🔊",
        "Настраивает озвучку и помогает с TTS",
        "ru",
        mapOf(
            "озвучка" to "Я могу озвучить любой распознанный текст! Настрой скорость и тон в настройках. Поддерживаемые языки: русский, английский, японский.",
            "голос" to "Скорость речи можно настроить от 0.5x до 2x. Тон — от 0.5 до 2.0. Попробуй разные комбинации для лучшего восприятия!",
            "не говорит" to "Проверь: 1) TTS включён? 2) Языковые данные установлены в системе? 3) Громкость не на нуле? В Settings есть кнопка проверки.",
            "японский" to "Для японского TTS нужно установить японский голос в настройках Android. Обычно Google TTS поддерживает японский.",
            "настройка" to "Лучшие настройки для манги: скорость 0.8x (немного медленнее для диалогов), тон 1.0. Для описаний можно ускорить до 1.2x."
        )
    )
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ttsService: TtsService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _activePersonas = MutableStateFlow<List<AiPersona>>(
        listOf(AiPersona.MANGA_EXPERT, AiPersona.OCR_HELPER, AiPersona.TTS_VOICE)
    )
    val activePersonas: StateFlow<List<AiPersona>> = _activePersonas.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speakingPersonaName = MutableStateFlow<String?>(null)
    val speakingPersonaName: StateFlow<String?> = _speakingPersonaName.asStateFlow()

    fun sendMessage(userText: String) {
        val userMessage = ChatMessage(
            text = userText,
            isFromUser = true
        )
        _messages.value = _messages.value + userMessage

        // Each active AI responds with relevant info
        viewModelScope.launch {
            val personas = _activePersonas.value
            for (persona in personas) {
                val response = generateAiResponse(persona, userText)
                val aiMessage = ChatMessage(
                    text = response,
                    isFromUser = false,
                    aiName = persona.displayName,
                    aiAvatarEmoji = persona.emoji
                )
                _messages.value = _messages.value + aiMessage

                // Speak AI responses in turn
                _speakingPersonaName.value = persona.displayName
                _isSpeaking.value = true
                ttsService.speak("${persona.displayName} говорит: $response")
                // Small delay between personas
                kotlinx.coroutines.delay(500)
            }
            _isSpeaking.value = false
            _speakingPersonaName.value = null
        }
    }

    private fun generateAiResponse(persona: AiPersona, userText: String): String {
        val lower = userText.lowercase()
        // Find best matching key
        val bestMatch = persona.sampleResponses.entries
            .filter { lower.contains(it.key) }
            .maxByOrNull { it.key.length }
        return bestMatch?.value ?: persona.sampleResponses.entries.first().value
    }

    fun togglePersona(persona: AiPersona, isActive: Boolean) {
        val current = _activePersonas.value
        _activePersonas.value = if (isActive) {
            current + persona
        } else {
            current - persona
        }
    }

    fun stopSpeaking() {
        ttsService.stop()
        _isSpeaking.value = false
        _speakingPersonaName.value = null
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.shutdown()
    }
}
