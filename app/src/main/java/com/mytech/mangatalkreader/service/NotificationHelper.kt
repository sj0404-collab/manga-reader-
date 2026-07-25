package com.mytech.mangatalkreader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mytech.mangatalkreader.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for showing Android notifications with recognized/translated text.
 *
 * Creates a dedicated notification channel "ocr_results" for OCR results.
 * Notifications show the Russian text (original or translated).
 * Tapping a notification opens the app.
 */
@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "ocr_results"
        private const val CHANNEL_NAME = "OCR Результаты"
        private const val CHANNEL_DESC = "Распознанный и переведённый текст манги"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** Create notification channel (required on Android 8+). */
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a notification with OCR/translated Russian text.
     *
     * @param title Notification title (e.g. "OCR: Страница 3")
     * @param russianText The Russian text to display (original or translated)
     * @param originalText The original recognized text (shown in subtitle if different)
     * @param id Unique ID for this notification (default uses incrementing counter)
     */
    fun showOcrNotification(
        title: String,
        russianText: String,
        originalText: String = "",
        id: Int = NOTIFICATION_ID_BASE + System.currentTimeMillis().toInt() % 10000
    ) {
        createChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigText = russianText.take(2000) // Notification text limit

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(russianText.take(60))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setSummaryText(if (originalText.isNotEmpty() && originalText != russianText) {
                        "Оригинал: ${originalText.take(40)}"
                    } else null)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }

    /**
     * Show a notification for AI translation status.
     */
    fun showTranslationNotification(
        originalLang: String,
        translatedText: String,
        id: Int = NOTIFICATION_ID_BASE + 500 + System.currentTimeMillis().toInt() % 10000
    ) {
        createChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("🤖 Перевод $originalLang → русский")
            .setContentText(translatedText.take(60))
            .setStyle(NotificationCompat.BigTextStyle().bigText(translatedText.take(2000)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }

    /** Cancel a specific notification. */
    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    /** Cancel all OCR notifications. */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
