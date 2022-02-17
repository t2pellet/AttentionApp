package com.commodorethrawn.attentionapp.service;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.activity.MainActivity
import com.commodorethrawn.attentionapp.util.DatabaseUtil
import com.commodorethrawn.attentionapp.util.PreferenceUtil
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    companion object {
        var notificationId = -1

        fun generateToken(ctx : Context) {
            FirebaseMessaging.getInstance().token.continueWith {
                PreferenceUtil.token = it.result
            }
        }

        fun createNotification(ctx : Context, title : String, body : String) {
            val notificationBuilder = NotificationCompat.Builder(ctx, ctx.getString(R.string.channelId))
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setChannelId(ctx.getString(R.string.channelId))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
            val notificationManager= ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId++, notificationBuilder.build())
        }
    }

    override fun onCreate() {
        super.onCreate();
        createNotificationChannel();
        notificationId = 0;
    }

    override fun onNewToken(s : String) {
        super.onNewToken(s)
        DatabaseUtil.coupleExists().continueWith {
            if (it.result) DatabaseUtil.addToDB()
        }
    }

    override fun onMessageReceived(remoteMessage : RemoteMessage) {
        when (remoteMessage.data["type"]) {
            "setup" -> {
                PreferenceUtil.parterName = remoteMessage.data["name"]!!
                createNotification(this, "Setup Complete", "Paired with ${remoteMessage.data["name"]}")
                setup()
            }
            "request" -> {
                startForegroundService(Intent(this, AttentionService::class.java))
            }
            "response" -> {
                createNotification(this, "Response Received!", "${PreferenceUtil.parterName} responded")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy();
        try {
            startService(Intent(baseContext, MessagingService::class.java))
        } catch (ignore : Exception) {
        }
    }

    /**
     * Creates the default notification channel
     */
    private fun createNotificationChannel() {
        val id = getString(R.string.channelId)
        val name = getString(R.string.channelName)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    /**
     * Sets up this messaging service
     */
    private fun setup() {
        PreferenceUtil.setupComplete = true
        val startIntent = Intent(this, MainActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(startIntent)
    }

}
