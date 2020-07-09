package com.commodorethrawn.attentionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Timer;
import java.util.TimerTask;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        updateToken(this, token);
    }

    public static void updateToken(Context ctx) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(result -> {
            if (result.isSuccessful()) {
                String token = result.getResult().getToken();
                updateToken(ctx, token);
            }
        });
    }

    public static void updateToken(Context ctx, String token) {
        boolean isSender = !ctx.getSharedPreferences("com.commodorethrawn.attentionapp", MODE_PRIVATE).getBoolean("isSender", false);
        if (isSender) FirebaseFunctions.getInstance().getHttpsCallable("updateBoyfriend").call(token);
        else FirebaseFunctions.getInstance().getHttpsCallable("updateGirlfriend").call(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "ATTENTION")
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat.from(this).notify(0, notificationBuilder.build());
        if (remoteMessage.getData().get("type").equals("attention")) {
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
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                "ATTENTION",
                "attention",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}
