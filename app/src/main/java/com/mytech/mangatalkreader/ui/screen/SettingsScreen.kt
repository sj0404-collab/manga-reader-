package com.mytech.mangatalkreader.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    var keepScreenOn by remember { mutableStateOf(false) }
    var showPageNumber by remember { mutableStateOf(true) }
    var ttsSpeed by remember { mutableFloatStateOf(1f) }
    var ttsPitch by remember { mutableFloatStateOf(1f) }
    var ocrSensitivity by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Общие настройки",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingSwitchItem(
            title = "Оставлять экран включённым",
            description = "Экран не будет выключаться во время чтения",
            isChecked = keepScreenOn,
            onCheckedChange = { keepScreenOn = it }
        )

        SettingSwitchItem(
            title = "Показывать номер страницы",
            description = "Отображать текущую страницу",
            isChecked = showPageNumber,
            onCheckedChange = { showPageNumber = it }
        )

        Text(
            text = "Настройки TTS (Озвучка)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        SettingSliderItem(
            title = "Скорость речи",
            description = "Регулировка скорости озвучки: ${(ttsSpeed * 100).toInt()}%",
            value = ttsSpeed,
            onValueChange = { ttsSpeed = it },
            valueRange = 0.5f..2f
        )

        SettingSliderItem(
            title = "Тон голоса",
            description = "Высота тона: ${(ttsPitch * 100).toInt()}%",
            value = ttsPitch,
            onValueChange = { ttsPitch = it },
            valueRange = 0.5f..2f
        )

        Text(
            text = "Настройки OCR (Распознавание)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        SettingSliderItem(
            title = "Чувствительность OCR",
            description = "Уровень точности: ${(ocrSensitivity * 100).toInt()}%",
            value = ocrSensitivity,
            onValueChange = { ocrSensitivity = it },
            valueRange = 0.1f..1f
        )

        SettingSwitchItem(
            title = "Автоматическое распознавание",
            description = "Распознавать текст при загрузке страницы",
            isChecked = true,
            onCheckedChange = { }
        )

        Text(
            text = "Информация о приложении",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        SettingInfoItem(
            title = "Версия приложения",
            value = "1.0.0"
        )

        SettingInfoItem(
            title = "Версия ОС",
            value = "Android 14+"
        )
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingSliderItem(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun SettingInfoItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
