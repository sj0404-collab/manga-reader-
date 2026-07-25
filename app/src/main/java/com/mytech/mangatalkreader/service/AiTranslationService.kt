package com.mytech.mangatalkreader.service

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OpenRouter AI API service for translation and chat.
 *
 * Supports:
 *  - Translation of any text to Russian
 *  - Language detection (is the text Russian?)
 *  - AI chat responses with persona context
 *
 * Uses OpenRouter's chat completions API which supports many models
 * including free/cheap ones like "openrouter/auto" (auto-routing to cheapest).
 *
 * API key must be set via [setApiKey] before making calls.
 * Get your key at: https://openrouter.ai/keys
 */
@Singleton
class AiTranslationService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {

    /** Secondary constructor for manual creation (no Hilt). */
    constructor() : this(
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build(),
        Gson()
    )

    var apiKey: String = ""
        private set

    /** Currently selected model ID. Default: auto (cheapest available). */
    var model: String = "openrouter/auto"
        private set

    /** OpenRouter API base URL. */
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun setApiKey(key: String) {
        apiKey = key
    }

    fun setModel(modelId: String) {
        model = modelId
    }

    /**
     * Check whether the given text is primarily Russian (Cyrillic).
     * Uses a simple heuristic: if more than 30% of letter characters are Cyrillic.
     */
    fun isRussian(text: String): Boolean {
        val letters = text.filter { it.isLetter() }
        if (letters.isEmpty()) return false
        val cyrillicCount = letters.count { char ->
            // Russian Cyrillic range: А-я (0x0410-0x044F) plus Ёё
            char.code in 0x0410..0x044F || char.code == 0x0401 || char.code == 0x0451
        }
        return cyrillicCount.toFloat() / letters.size > 0.3f
    }

    /**
     * Translate text to Russian using OpenRouter AI.
     * If the text is already Russian, returns it unchanged.
     * If apiKey is empty, returns the original text with a note.
     *
     * @param text Text to translate
     * @param sourceLang Hint about source language (ja, zh, ko, en, etc.)
     * @return Russian text (translated or original if already Russian)
     */
    suspend fun translateToRussian(text: String, sourceLang: String = ""): String =
        withContext(Dispatchers.IO) {
            if (text.isBlank()) return@withContext ""
            if (isRussian(text)) return@withContext text
            if (apiKey.isBlank()) return@withContext "$text [⚠️ Нет API ключа OpenRouter — добавьте в настройках]"

            val systemPrompt = """You are a professional translator. Translate the given text to Russian.
Preserve the meaning, tone, and style. If the text contains manga dialogue, keep it natural and expressive in Russian.
Only output the Russian translation, nothing else. Do not add explanations or notes."""

            val userPrompt = if (sourceLang.isNotEmpty()) {
                "Translate this $sourceLang text to Russian:\n$text"
            } else {
                "Translate this text to Russian:\n$text"
            }

            val request = ChatRequest(
                model = model,
                messages = listOf(
                    Message(role = "system", content = systemPrompt),
                    Message(role = "user", content = userPrompt)
                ),
                temperature = 0.3,
                maxTokens = 1024
            )

            val body = gson.toJson(request).toRequestBody(jsonMediaType)

            val httpRequest = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://mangatalkreader.app")
                .addHeader("X-Title", "MangaTalkReader")
                .post(body)
                .build()

            try {
                val response = okHttpClient.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext "$text [⚠️ Ошибка перевода: ${response.code}]"
                }

                val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
                val translatedText = chatResponse.choices?.firstOrNull()?.message?.content?.trim()
                    ?: return@withContext "$text [⚠️ AI вернул пустой ответ]"

                translatedText
            } catch (e: Exception) {
                "$text [⚠️ Ошибка сети: ${e.message}]"
            }
        }

    /**
     * Generate an AI chat response with persona context using OpenRouter.
     * Falls back to local responses if apiKey is empty.
     */
    suspend fun chatWithPersona(
        personaName: String,
        personaDescription: String,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext ""

        val systemPrompt = """You are $personaName — $personaDescription.
Respond in Russian. Be helpful, friendly, and knowledgeable about your area.
Keep responses concise (2-4 sentences). Use emojis occasionally."""

        val request = ChatRequest(
            model = model,
            messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = userMessage)
            ),
            temperature = 0.7,
            maxTokens = 256
        )

        val body = gson.toJson(request).toRequestBody(jsonMediaType)

        val httpRequest = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://mangatalkreader.app")
            .addHeader("X-Title", "MangaTalkReader")
            .post(body)
            .build()

        try {
            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext "Ошибка AI: ${response.code}. Попробуйте позже."
            }

            val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
            chatResponse.choices?.firstOrNull()?.message?.content?.trim()
                ?: "AI не смог ответить. Попробуйте ещё."
        } catch (e: Exception) {
            "Ошибка сети: ${e.message}. Проверьте интернет и API ключ."
        }
    }

    // ---- Gson data classes for OpenRouter API ----

    data class ChatRequest(
        val model: String,
        val messages: List<Message>,
        val temperature: Double = 0.7,
        @SerializedName("max_tokens")
        val maxTokens: Int = 256
    )

    data class Message(
        val role: String,
        val content: String
    )

    data class ChatResponse(
        val id: String? = null,
        val choices: List<Choice>? = null,
        val usage: Usage? = null
    )

    data class Choice(
        val index: Int = 0,
        val message: Message? = null,
        @SerializedName("finish_reason")
        val finishReason: String? = null
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int = 0,
        @SerializedName("completion_tokens")
        val completionTokens: Int = 0,
        @SerializedName("total_tokens")
        val totalTokens: Int = 0
    )
}
