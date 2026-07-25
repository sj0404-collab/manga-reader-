package com.mytech.mangatalkreader.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytech.mangatalkreader.service.OcrService
import com.mytech.mangatalkreader.service.TtsService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val ocrService = remember { OcrService(context) }
    val ttsService = remember { TtsService(context) }

    var ocrEnabled by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var speechRate by remember { mutableFloatStateOf(1.0f) }
    var pitchRate by remember { mutableFloatStateOf(1.0f) }
    var ocrLanguage by remember { mutableStateOf("en") }

    val ocrLanguages = listOf(
        "en" to "English",
        "ja" to "日本語",
        "zh" to "中文",
        "ko" to "한국어"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // OCR Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔤 Распознавание текста (OCR)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Включить OCR")
                        Switch(checked = ocrEnabled, onCheckedChange = { ocrEnabled = it })
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Язык OCR:", fontWeight = FontWeight.Medium)
                    ocrLanguages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name)
                            Switch(
                                checked = ocrLanguage == code,
                                onCheckedChange = { if (it) ocrLanguage = code }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "ML Kit работает полностью офлайн — модели встроены в приложение",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TTS Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 Синтез речи (TTS)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Включить озвучку")
                        Switch(checked = ttsEnabled, onCheckedChange = { ttsEnabled = it })
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Скорость речи: ${String.format("%.1f", speechRate)}x")
                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Высота голоса: ${String.format("%.1f", pitchRate)}")
                    Slider(
                        value = pitchRate,
                        onValueChange = { pitchRate = it },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (ttsEnabled) {
                                ttsService.setSpeechRate(speechRate)
                                ttsService.setPitchRate(pitchRate)
                                ttsService.speak("Привет! Это Manga Talk Reader. Распознавание текста и озвучка работают офлайн.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔈 Проверить озвучку")
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "TTS работает офлайн если установлены голосовые данные в настройках Android",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
