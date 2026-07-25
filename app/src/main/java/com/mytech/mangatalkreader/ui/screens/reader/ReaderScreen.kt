package com.mytech.mangatalkreader.ui.screens.reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mytech.mangatalkreader.service.AiTranslationService
import com.mytech.mangatalkreader.service.NotificationHelper
import com.mytech.mangatalkreader.service.OcrLanguage
import com.mytech.mangatalkreader.service.OcrResult
import com.mytech.mangatalkreader.service.OcrService
import com.mytech.mangatalkreader.service.TtsService
import com.mytech.mangatalkreader.ui.viewmodel.ReaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Locale

/** Data class for OCR overlay on the manga page image. */
data class OcrOverlay(
    val text: String,
    val russianText: String,  // translated or original if already Russian
    val boundingBox: Rect?,    // in bitmap pixel coordinates
    val confidence: Float
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val ocrService = remember { OcrService(context) }
    val ttsService = remember { TtsService(context) }
    val aiService = remember { AiTranslationService() }
    val notificationHelper = remember { NotificationHelper(context) }

    // Load API key from SharedPreferences
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("manga_settings", 0)
        val apiKey = prefs.getString("openrouter_api_key", "") ?: ""
        aiService.setApiKey(apiKey)
        val model = prefs.getString("openrouter_model", "openrouter/auto") ?: "openrouter/auto"
        aiService.setModel(model)

        // Load OCR language preference
        val ocrLangCode = prefs.getString("ocr_language", "en") ?: "en"
        ocrService.setLanguage(Locale(ocrLangCode))
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    var isOcrActive by remember { mutableStateOf(false) }
    var isTtsActive by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var russianText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var ocrOverlays by remember { mutableStateOf<List<OcrOverlay>>(emptyList()) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageDisplaySize by remember { mutableStateOf<IntSize>(IntSize.Zero) }

    val pageUrls = viewModel.pageUrls
    val pagerState = rememberPagerState(pageCount = { pageUrls.size })

    LaunchedEffect(pagerState.currentPage) {
        currentPageIndex = pagerState.currentPage
        viewModel.saveProgress(currentPageIndex)
        // Clear OCR overlay when changing pages
        if (isOcrActive) {
            ocrOverlays = emptyList()
            recognizedText = ""
            russianText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Страница ${currentPageIndex + 1} / ${pageUrls.size}") },
                navigationIcon = {
                    IconButton(onClick = { ttsService.stop(); onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isOcrActive = !isOcrActive
                        if (isOcrActive) {
                            ocrOverlays = emptyList()
                            recognizedText = ""
                            russianText = ""
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val url = URL(pageUrls[currentPageIndex])
                                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                                        ?: return@launch
                                    currentBitmap = bitmap

                                    val ocrResults: List<OcrResult> = ocrService.recognizeFromBitmapInternal(bitmap)
                                    recognizedText = ocrResults.joinToString("\n") { it.text }

                                    // Check if already Russian
                                    val isAlreadyRussian = aiService.isRussian(recognizedText)

                                    if (isAlreadyRussian) {
                                        // Text is already Russian — show directly, notify, TTS
                                        russianText = recognizedText
                                        withContext(Dispatchers.Main) {
                                            notificationHelper.showOcrNotification(
                                                title = "OCR: Стр. ${currentPageIndex + 1} 🇷🇺",
                                                russianText = recognizedText
                                            )
                                        }
                                        // Create overlays with filled boxes
                                        ocrOverlays = ocrResults.map { result ->
                                            OcrOverlay(
                                                text = result.text,
                                                russianText = result.text,
                                                boundingBox = result.boundingBox,
                                                confidence = result.confidence
                                            )
                                        }

                                        // TTS in Russian
                                        ttsService.setLanguage(Locale("ru", "RU"))
                                        if (isTtsActive) {
                                            ttsService.speak(russianText)
                                        }
                                    } else {
                                        // Not Russian — translate via AI
                                        isTranslating = true
                                        val translated = aiService.translateToRussian(
                                            text = recognizedText,
                                            sourceLang = ocrService.currentLanguage.scriptTag
                                        )
                                        russianText = translated
                                        isTranslating = false

                                        // Create overlays with translated text
                                        ocrOverlays = ocrResults.map { result ->
                                            // Try to translate each block individually for better overlay text
                                            val blockRussian = if (aiService.isRussian(result.text)) {
                                                result.text
                                            } else {
                                                // Use the full translated text for each block
                                                // (individual block translation would be too many API calls)
                                                translated
                                            }
                                            OcrOverlay(
                                                text = result.text,
                                                russianText = blockRussian,
                                                boundingBox = result.boundingBox,
                                                confidence = result.confidence
                                            )
                                        }

                                        // Notification with Russian text
                                        withContext(Dispatchers.Main) {
                                            notificationHelper.showTranslationNotification(
                                                originalLang = ocrService.currentLanguage.displayName,
                                                translatedText = translated,
                                            )
                                            notificationHelper.showOcrNotification(
                                                title = "OCR: Стр. ${currentPageIndex + 1} (перевод)",
                                                russianText = translated,
                                                originalText = recognizedText
                                            )
                                        }

                                        // TTS in Russian
                                        ttsService.setLanguage(Locale("ru", "RU"))
                                        if (isTtsActive && translated.isNotBlank()) {
                                            ttsService.speak(translated)
                                        }
                                    }
                                } catch (e: Exception) {
                                    recognizedText = "Ошибка OCR: ${e.message}"
                                    russianText = "Ошибка: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            ocrOverlays = emptyList()
                            recognizedText = ""
                            russianText = ""
                            currentBitmap = null
                        }
                    }) {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = "OCR",
                            tint = if (isOcrActive) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        isTtsActive = !isTtsActive
                        if (isTtsActive && russianText.isNotBlank()) {
                            ttsService.setLanguage(Locale("ru", "RU"))
                            ttsService.speak(russianText)
                        } else {
                            ttsService.stop()
                        }
                    }) {
                        Icon(
                            if (isTtsActive) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "TTS",
                            tint = if (isTtsActive) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val width = size.width
                                if (offset.x < width / 3) {
                                    scope.launch {
                                        pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                                    }
                                } else if (offset.x > width * 2 / 3) {
                                    scope.launch {
                                        pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(pageUrls.size - 1))
                                    }
                                }
                            }
                        )
                    }
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize().onGloballyPositioned { coords ->
                        imageDisplaySize = coords.size
                    },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(pageUrls[pageIndex])
                            .crossfade(true)
                            .build(),
                        contentDescription = "Страница ${pageIndex + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // OCR Overlay: filled bounding boxes on top of the image
                    if (isOcrActive && ocrOverlays.isNotEmpty() && currentBitmap != null && pageIndex == currentPageIndex) {
                        val bitmap = currentBitmap!!
                        val displaySize = imageDisplaySize

                        // Calculate scale and offset to map bitmap coords to display coords
                        // ContentScale.Fit: image is scaled uniformly to fit within the bounds
                        val scaleX = displaySize.width.toFloat() / bitmap.width
                        val scaleY = displaySize.height.toFloat() / bitmap.height
                        val scale = minOf(scaleX, scaleY) // Fit uses the smaller scale

                        // Calculate the offset (image is centered within the display area)
                        val imageWidth = bitmap.width * scale
                        val imageHeight = bitmap.height * scale
                        val offsetX = (displaySize.width - imageWidth) / 2f
                        val offsetY = (displaySize.height - imageHeight) / 2f

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            for (overlay in ocrOverlays) {
                                val bb = overlay.boundingBox
                                if (bb != null) {
                                    // Convert bitmap coordinates to display coordinates
                                    val left = bb.left * scale + offsetX
                                    val top = bb.top * scale + offsetY
                                    val right = bb.right * scale + offsetX
                                    val bottom = bb.bottom * scale + offsetY

                                    val rect = ComposeRect(
                                        left = left,
                                        top = top,
                                        right = right,
                                        bottom = bottom
                                    )

                                    // Draw FILLED bounding box with semi-transparent highlight
                                    // Yellow/orange fill for visibility
                                    drawRect(
                                        color = Color(0xFFFF9800).copy(alpha = 0.35f), // Orange with 35% opacity
                                        topLeft = Offset(rect.left, rect.top),
                                        size = Size(rect.width, rect.height)
                                    )

                                    // Draw solid border around the box
                                    drawRect(
                                        color = Color(0xFFFF5722).copy(alpha = 0.85f), // Deep orange border
                                        topLeft = Offset(rect.left, rect.top),
                                        size = Size(rect.width, rect.height),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    if (isLoading && isOcrActive) {
                        CircularProgressIndicator()
                    }
                    if (isTranslating) {
                        CircularProgressIndicator()
                    }
                }
            }

            // OCR text panel at bottom
            if (isOcrActive) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(0.3f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        if (russianText.isNotBlank()) {
                            // Show Russian text (translated or original)
                            Text(
                                text = russianText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (recognizedText.isNotBlank() && recognizedText != russianText) {
                            // Show original text if different from Russian
                            Text(
                                text = "Оригинал: $recognizedText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (russianText.isBlank() && recognizedText.isBlank() && !isLoading) {
                            Text(
                                text = "Текст не найден на этой странице",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
