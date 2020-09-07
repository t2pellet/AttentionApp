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

import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {

    private SharedPreferences preferences;
    public static int notificationId;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        notificationId = 0;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if (preferences.contains("isBoyfriend")) {
            String reference = preferences.getBoolean("isBoyfriend", false) ? "boyfriend" : "girlfriend";
            FirebaseDatabase.getInstance().getReference(reference).setValue(s);
        }
    }

    public static void generateToken(Context ctx) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(result -> {
            ctx.getSharedPreferences("attentionapp", MODE_PRIVATE).edit()
                    .putString("token", result.getToken()).apply();
        });
    }

    public static void createNotification(Context ctx, String title, String body, int id) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(ctx, ctx.getString(R.string.channelId))
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setChannelId(ctx.getString(R.string.channelId))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL);
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationBuilder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Objects.equals(remoteMessage.getData().get("type"), "attention")) {
            preferences.edit().putString("parterName", remoteMessage.getData().get("name")).apply();
            startForegroundService(new Intent(this, AttentionService.class));
        } else {
            if (Objects.equals(remoteMessage.getData().get("type"), "setup")) {
                System.out.println("SETUP DONE");
                setup();
            }
            createNotification(
                    this,
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"),
                    ++notificationId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            startService(new Intent(getBaseContext(), MessagingService.class));
        } catch (Exception ignore) {
        }
    }

    /**
     * Creates the default notification channel
     */
    private void createNotificationChannel() {
        String id = getString(R.string.channelId);
        String name = getString(R.string.channelName);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    /**
     * Sets up this messaging service
     */
    private void setup() {
        preferences.edit().putBoolean("isSetup", true).apply();
        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
    }

}
