package com.mytech.mangatalkreader.ui.screens.reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mytech.mangatalkreader.service.OcrService
import com.mytech.mangatalkreader.service.TtsService
import com.mytech.mangatalkreader.ui.viewmodel.ReaderViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ocrService = remember { OcrService(context) }
    val ttsService = remember { TtsService(context) }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    var isOcrActive by remember { mutableStateOf(false) }
    var isTtsActive by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val pageUrls = viewModel.pageUrls
    val pagerState = rememberPagerState(pageCount = { pageUrls.size })

    LaunchedEffect(pagerState.currentPage) {
        currentPageIndex = pagerState.currentPage
        viewModel.saveProgress(currentPageIndex)
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
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val url = URL(pageUrls[currentPageIndex])
                                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                                    val text = ocrService.recognizeFromBitmap(bitmap)
                                    recognizedText = text
                                    if (isTtsActive && text.isNotBlank()) {
                                        ttsService.speak(text)
                                    }
                                } catch (e: Exception) {
                                    recognizedText = "Ошибка OCR: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            recognizedText = ""
                        }
                    }) {
                        Icon(Icons.Default.Translate, contentDescription = "OCR")
                    }
                    IconButton(onClick = {
                        isTtsActive = !isTtsActive
                        if (isTtsActive && recognizedText.isNotBlank()) {
                            ttsService.speak(recognizedText)
                        } else {
                            ttsService.stop()
                        }
                    }) {
                        Icon(
                            if (isTtsActive) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "TTS"
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(pageUrls[pageIndex])
                            .crossfade(true)
                            .build(),
                        contentDescription = "Страница ${pageIndex + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (isOcrActive && recognizedText.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(0.3f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recognizedText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
