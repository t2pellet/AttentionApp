package com.commodorethrawn.attentionapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.activity.MainActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.lang.Exception

import java.util.Objects;

class MessagingService : FirebaseMessagingService() {

    private lateinit var preferences : SharedPreferences

    companion object {
        var notificationId = -1

        fun generateToken(ctx : Context) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                ctx.getSharedPreferences("attentionapp", MODE_PRIVATE).edit()
                        .putString("token", it.token).apply()
            }
        }

        fun createNotification(ctx : Context, title : String, body : String, id : Int) {
            val notificationBuilder = NotificationCompat.Builder(ctx, ctx.getString(R.string.channelId))
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setChannelId(ctx.getString(R.string.channelId))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
            val notificationManager= ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(id, notificationBuilder.build())
        }
    }

    override fun onCreate() {
        super.onCreate();
        createNotificationChannel();
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        notificationId = 0;
    }

    override fun onNewToken(s : String?) {
        super.onNewToken(s);
        if (preferences.contains("isBoyfriend")) {
            val reference = if (preferences.getBoolean("isBoyfriend", false)) "boyfriend" else "girlfriend"
            FirebaseDatabase.getInstance().getReference(reference).setValue(s);
        }
    }

    override fun onMessageReceived(remoteMessage : RemoteMessage?) {
        if (Objects.equals(remoteMessage!!.data["type"], "attention")) {
            preferences.edit().putString("parterName", remoteMessage.data["name"]).apply();
            startForegroundService(Intent(this, AttentionService::class.java))
        } else {
            if (Objects.equals(remoteMessage.data["type"], "setup")) {
                System.out.println("SETUP DONE");
                setup();
            }
            createNotification(
                    this,
                    remoteMessage.data["title"]!!,
                    remoteMessage.data["body"]!!,
                    ++notificationId)
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
        preferences.edit().putBoolean("isSetup", true).apply()
        val startIntent = Intent(this, MainActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(startIntent)
    }

}
