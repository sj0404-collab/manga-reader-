package com.mytech.mangatalkreader.domain.service

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mytech.mangatalkreader.data.model.TextBlock
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

    suspend fun recognizeText(bitmap: Bitmap): List<TextBlock> = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val task = recognizer.process(image)

            var textBlocks = listOf<TextBlock>()
            task.addOnSuccessListener { visionText ->
                textBlocks = visionText.textBlocks.mapIndexed { index, block ->
                    val boundingBox = block.boundingBox
                    TextBlock(
                        id = 0,
                        chapterId = 0,
                        pageNumber = 0,
                        text = block.text,
                        type = detectTextType(block.text),
                        language = detectLanguage(block.text),
                        x = boundingBox?.left?.toFloat() ?: 0f,
                        y = boundingBox?.top?.toFloat() ?: 0f,
                        width = (boundingBox?.right?.toFloat() ?: 0f) - (boundingBox?.left?.toFloat() ?: 0f),
                        height = (boundingBox?.bottom?.toFloat() ?: 0f) - (boundingBox?.top?.toFloat() ?: 0f),
                        textColor = -1,
                        backgroundColor = null,
                        fontSize = calculateFontSize(block),
                        isManual = false
                    )
                }
            }.addOnFailureListener { exception ->
                Timber.e(exception, "OCR recognition failed")
            }

            textBlocks
        } catch (e: Exception) {
            Timber.e(e, "Error in OCR service")
            emptyList()
        }
    }

    private fun detectTextType(text: String): String {
        return when {
            text.length > 50 -> "description"
            text.contains("※") || text.contains("TN:") -> "comment"
            else -> "dialog"
        }
    }

    private fun detectLanguage(text: String): String {
        return when {
            text.any { it.code in 0x3040..0x309F || it.code in 0x30A0..0x30FF } -> "ja"
            text.any { it.code in 0x0400..0x04FF } -> "ru"
            else -> "en"
        }
    }

    private fun calculateFontSize(block: com.google.mlkit.vision.text.TextBlock): Float {
        val boundingBox = block.boundingBox
        return if (boundingBox != null) {
            (boundingBox.height() / 2).toFloat()
        } else {
            16f
        }
    }
}
