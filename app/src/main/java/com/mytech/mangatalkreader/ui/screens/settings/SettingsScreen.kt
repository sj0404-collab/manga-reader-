package com.mytech.mangatalkreader.ui.screens.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.core.content.ContextCompat
import com.mytech.mangatalkreader.service.AiTranslationService
import com.mytech.mangatalkreader.service.NotificationHelper
import com.mytech.mangatalkreader.service.OcrLanguage
import com.mytech.mangatalkreader.service.OcrService
import com.mytech.mangatalkreader.service.TtsService
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("manga_settings", 0) }
    val ocrService = remember { OcrService(context) }
    val ttsService = remember { TtsService(context) }
    val aiService = remember { AiTranslationService() }
    val notificationHelper = remember { NotificationHelper(context) }

    // Notification permission launcher (Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
        if (granted) {
            notificationHelper.createChannel()
        }
    }

    var ocrEnabled by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var speechRate by remember { mutableFloatStateOf(prefs.getFloat("tts_speech_rate", 1.0f)) }
    var pitchRate by remember { mutableFloatStateOf(prefs.getFloat("tts_pitch_rate", 1.0f)) }
    var ocrLanguage by remember { mutableStateOf(prefs.getString("ocr_language", "en") ?: "en") }

    var openRouterApiKey by remember { mutableStateOf(prefs.getString("openrouter_api_key", "") ?: "") }
    var openRouterModel by remember { mutableStateOf(prefs.getString("openrouter_model", "openrouter/auto") ?: "openrouter/auto") }
    var autoTranslate by remember { mutableStateOf(prefs.getBoolean("auto_translate", true)) }
    var showNotifications by remember { mutableStateOf(prefs.getBoolean("show_notifications", true)) }

    val ocrLanguages = listOf(
        "en" to "English",
        "ja" to "日本語",
        "zh" to "中文",
        "ko" to "한국어",
        "de" to "देवनागरी"
    )

    val aiModels = listOf(
        "openrouter/auto" to "Auto ( cheapest )",
        "openai/gpt-4o-mini" to "GPT-4o Mini",
        "google/gemini-2.0-flash-001" to "Gemini 2.0 Flash",
        "meta-llama/llama-3.1-8b-instruct:free" to "Llama 3.1 8B ( free )",
        "mistralai/mistral-7b-instruct:free" to "Mistral 7B ( free )",
        "qwen/qwen-2-7b-instruct:free" to "Qwen 2 7B ( free )"
    )

    // Save settings when changed
    fun saveSettings() {
        prefs.edit()
            .putString("openrouter_api_key", openRouterApiKey)
            .putString("openrouter_model", openRouterModel)
            .putBoolean("auto_translate", autoTranslate)
            .putBoolean("show_notifications", showNotifications)
            .putString("ocr_language", ocrLanguage)
            .putFloat("tts_speech_rate", speechRate)
            .putFloat("tts_pitch_rate", pitchRate)
            .apply()

        // Update services
        aiService.setApiKey(openRouterApiKey)
        aiService.setModel(openRouterModel)
        ttsService.setSpeechRate(speechRate)
        ttsService.setPitch(pitchRate)
        ocrService.setLanguage(Locale(ocrLanguage))
    }

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
            // OpenRouter AI Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🤖 OpenRouter AI ( Перевод + Чат )",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "API ключ для OpenRouter — используется для перевода OCR текста на русский и AI-чат. Получите ключ: openrouter.ai/keys",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = openRouterApiKey,
                        onValueChange = { openRouterApiKey = it; saveSettings() },
                        label = { Text("API ключ OpenRouter") },
                        placeholder = { Text("sk-or-...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Модель AI:", fontWeight = FontWeight.Medium)
                    aiModels.forEach { (modelId, modelName) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(modelName, fontSize = 14.sp)
                            Switch(
                                checked = openRouterModel == modelId,
                                onCheckedChange = { if (it) { openRouterModel = modelId; saveSettings() } }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Авто-перевод на русский")
                        Switch(
                            checked = autoTranslate,
                            onCheckedChange = { autoTranslate = it; saveSettings() }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Уведомления с текстом")
                        Switch(
                            checked = showNotifications,
                            onCheckedChange = {
                                showNotifications = it
                                saveSettings()
                                if (it && !hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            saveSettings()
                            notificationHelper.createChannel()
                            notificationHelper.showOcrNotification(
                                title = "🤖 Проверка AI",
                                russianText = if (openRouterApiKey.isNotBlank()) {
                                    "API ключ настроен! AI перевод и чат активны."
                                } else {
                                    "⚠️ API ключ не указан. Добавьте ключ OpenRouter для перевода."
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧪 Проверить AI + уведомления")
                    }
                }
            }

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
                                onCheckedChange = { if (it) { ocrLanguage = code; saveSettings() } }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "OCR показывает заполненные рамки на тексте. Если текст не русский — AI переведёт и уведомление покажет русский перевод, после TTS озвучит.",
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
                        onValueChange = { speechRate = it; saveSettings() },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Высота голоса: ${String.format("%.1f", pitchRate)}")
                    Slider(
                        value = pitchRate,
                        onValueChange = { pitchRate = it; saveSettings() },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            ttsService.setSpeechRate(speechRate)
                            ttsService.setPitch(pitchRate)
                            ttsService.setLanguage(Locale("ru", "RU"))
                            ttsService.speak("Привет! Это Manga Talk Reader. Распознавание текста, перевод и озвучка работают на русском.")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔈 Проверить озвучку ( русский )")
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "TTS озвучивает переведённый русский текст. Работает офлайн если голосовые данные установлены.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
