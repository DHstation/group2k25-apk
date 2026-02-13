package com.quantiumcode.group2k25.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.MainActivity
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.data.api.models.RegisterDeviceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.app_name)
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val data = message.data

        showNotification(title, body, data)
    }

    private fun registerToken(token: String) {
        val app = application as? App ?: return
        val authToken = app.container.tokenManager.getToken() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.container.apiService.registerDevice(RegisterDeviceRequest(token, "android"))
            } catch (e: Exception) {
                // Silently fail - will retry on next app open
            }
        }
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val channelId = "group2k25_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Group 2k25",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificacoes do Group 2k25"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // Pass notification data to MainActivity
            putExtra("navigate_to", data["type"] ?: "")
            putExtra("installment_id", data["installment_id"] ?: "")
            putExtra("contract_id", data["contract_id"] ?: "")
        }

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(
            this, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(requestCode, notification)
    }
}
