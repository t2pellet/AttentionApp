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
    private int notificationId;

    public static void generateToken(Context ctx) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(result -> {
            ctx.getSharedPreferences("attentionapp", MODE_PRIVATE).edit()
                    .putString("token", result.getToken()).apply();
        });
    }

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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Objects.equals(remoteMessage.getData().get("type"), "setup")) setup();
        if (Objects.equals(remoteMessage.getData().get("type"), "attention")) {
            startForegroundService(new Intent(this, AttentionService.class));
        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.channelId))
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setContentText(remoteMessage.getData().get("body"))
                            .setChannelId(getString(R.string.channelId))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_CALL);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notificationBuilder.build());
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
        String token = preferences.getString("token", "");
        if (token.isEmpty()) {
            generateToken(this);
            token = preferences.getString("token", "");
        }
        FirebaseDatabase.getInstance().getReference("girlfriend")
                .setValue(token);
        preferences.edit().putBoolean("isSetup", true).apply();
        preferences.edit().putBoolean("isBoyfriend", false).apply();
        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
    }

}
