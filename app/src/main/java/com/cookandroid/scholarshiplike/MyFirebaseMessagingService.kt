package com.cookandroid.scholarshiplike

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.size > 0) {
            showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
        }
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!
                    .body
            )
        }
    }

    private fun getCustomDesign(title: String?, message: String?): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.noti_title, title)
        remoteViews.setTextViewText(R.id.noti_message, message)
        remoteViews.setImageViewResource(R.id.noti_icon, R.mipmap.ic_launcher)
        return remoteViews
    }

    private fun showNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val channel_id = "channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(
            applicationContext, channel_id
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(uri)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setContent(getCustomDesign(title, message))
        } else {
            builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(uri, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }
}