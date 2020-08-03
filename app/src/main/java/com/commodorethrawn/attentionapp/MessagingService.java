package com.commodorethrawn.attentionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.channelId))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setChannelId(getString(R.string.channelId));
        NotificationManagerCompat.from(this).notify(0, notificationBuilder.build());
        if (Objects.equals(remoteMessage.getData().get("type"), "attention")) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent attentionIntent = new Intent(MessagingService.this, AttentionActivity.class);
                    attentionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    attentionIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                    startActivity(attentionIntent);

                }
            }, 2000);
        } else if (Objects.equals(remoteMessage.getData().get("type"), "boyfriend")) {
                SharedPreferences preferences = getSharedPreferences("com.commodorethrawn.attentionapp", MODE_PRIVATE);
                preferences.edit().putBoolean("isSetup", true).apply();
                preferences.edit().putBoolean("isSender", true).apply();
                preferences.edit().putBoolean("isFirstLaunch", false).apply();
                Intent startIntent = new Intent(this, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startIntent);
        }
    }

    private void createNotificationChannel() {
        String id = getString(R.string.channelId);
        String name = getString(R.string.channelName);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
