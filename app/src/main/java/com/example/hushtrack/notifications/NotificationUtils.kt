package com.example.hushtrack.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.hushtrack.R

fun showStatusUpdateNotification(context: Context, newStatus: String) {
    val channelId = "report_updates"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Report Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.maincon)
        .setContentTitle("Report Status Updated")
        .setContentText("Your report status is now: $newStatus")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    manager.notify(System.currentTimeMillis().toInt(), notification)
}