package com.mytech.mangatalkreader.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of OCR text recognition containing the recognized text,
 * its bounding box, and a confidence score.
 */
data class OcrResult(
    val text: String,
    val boundingBox: Rect?,
    val confidence: Float
)

/**
 * Supported OCR languages backed by ML Kit on-device models.
 */
enum class OcrLanguage(val scriptTag: String) {
    ENGLISH("latn"),
    CHINESE("zh"),
    JAPANESE("ja"),
    KOREAN("ko"),
    DEVANAGARI("de");

    companion object {
        fun fromLocale(locale: Locale): OcrLanguage {
            return when {
                locale.language == Locale.JAPANESE.language -> JAPANESE
                locale.language == Locale.KOREAN.language -> KOREAN
                locale.language == Locale.CHINESE.language -> CHINESE
                locale.language == "hi" || locale.language == "mr" || locale.language == "ne" -> DEVANAGARI
                else -> ENGLISH
            }
        }
    }
}

/**
 * OCR service powered by Google ML Kit Text Recognition.
 *
 * All ML Kit recognizers run fully on-device — no network is required.
 * Each language has its own dedicated on-device model that must be
 * downloaded the first time it is used (ML Kit handles this automatically
 * via Google Play Services).
 *
 * Usage:
 *   - Call [recognizeText] with a content URI to get structured results.
 *   - Call [recognizeFromBitmap] with a Bitmap to get plain text.
 *   - Switch languages with [setLanguage].
 */
@Singleton
class OcrService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var currentLanguage: OcrLanguage = OcrLanguage.ENGLISH

    /** Map of lazily-created recognizers keyed by language. */
    private val recognizers = mutableMapOf<OcrLanguage, com.google.mlkit.vision.text.TextRecognizer>()

    /**
     * Return (or create) the recognizer for the given language.
     * ML Kit TextRecognizer instances are thread-safe and should be
     * reused across calls.
     */
    private fun getRecognizer(language: OcrLanguage): com.google.mlkit.vision.text.TextRecognizer {
        return recognizers.getOrPut(language) {
            TextRecognition.getClient(
                when (language) {
                    OcrLanguage.ENGLISH -> TextRecognizerOptions.DEFAULT_OPTIONS
                    OcrLanguage.CHINESE -> ChineseTextRecognizerOptions.Builder().build()
                    OcrLanguage.JAPANESE -> JapaneseTextRecognizerOptions.Builder().build()
                    OcrLanguage.KOREAN -> KoreanTextRecognizerOptions.Builder().build()
                    OcrLanguage.DEVANAGARI -> DevanagariTextRecognizerOptions.Builder().build()
                }
            )
        }
    }

    /**
     * Change the active OCR language.
     * This does NOT close the previous recognizer — it remains cached
     * in case the user switches back.
     */
    fun setLanguage(language: OcrLanguage) {
        currentLanguage = language
    }

    /** Convenience overload accepting a Java [Locale]. */
    fun setLanguage(locale: Locale) {
        setLanguage(OcrLanguage.fromLocale(locale))
    }

    /**
     * Recognize text from a content URI.
     *
     * The URI should point to an image that the app can open through
     * [ContentResolver.openInputStream] (e.g. a file:// or content:// URI).
     *
     * Returns a list of [OcrResult], one per text block detected.
     */
    suspend fun recognizeText(imageUri: Uri): List<OcrResult> {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open InputStream for URI: $imageUri")

        val bitmap = try {
            android.graphics.BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalStateException("Failed to decode Bitmap from URI: $imageUri")
        } finally {
            inputStream.close()
        }

        return recognizeFromBitmapInternal(bitmap)
    }

    /**
     * Recognize text from a [Bitmap] and return the full plain-text result.
     *
     * All detected text blocks are concatenated with newlines.
     */
    suspend fun recognizeFromBitmap(bitmap: Bitmap): String {
        return recognizeFromBitmapInternal(bitmap)
            .joinToString(separator = "\n") { it.text }
    }

    /**
     * Internal implementation shared by both public methods.
     * Returns structured [OcrResult] objects.
     */
    private suspend fun recognizeFromBitmapInternal(bitmap: Bitmap): List<OcrResult> {
        val recognizer = getRecognizer(currentLanguage)
        val inputImage = InputImage.fromBitmap(bitmap, 0 /* rotationOverride */)

        val visionText: Text = try {
            recognizer.process(inputImage).await()
        } catch (e: Exception) {
            // Wrap ML Kit exceptions in a runtime exception so callers
            // can handle them uniformly.
            throw RuntimeException("OCR recognition failed for language=$currentLanguage", e)
        }

        val results = mutableListOf<OcrResult>()

        // Walk the hierarchy: Text -> TextBlock -> Line -> Element
        // We collect results at the Line level because it provides a
        // good balance between granularity and readability.
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val confidence = line.confidence ?: 0f
                results.add(
                    OcrResult(
                        text = line.text,
                        boundingBox = line.boundingBox,
                        confidence = confidence
                    )
                )
            }
        }

        return results
    }

    /**
     * Release all cached recognizers.
     * Call this when the service is no longer needed (e.g. in
     * ViewModel.onCleared or Application.onTerminate).
     */
    fun close() {
        recognizers.values.forEach { it.close() }
        recognizers.clear()
    }
}
