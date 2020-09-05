package com.commodorethrawn.attentionapp.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.activity.AttentionActivity;

import java.io.IOException;

public class AttentionService extends Service {

    private int notificationId;
    private Uri alarm;
    private MediaPlayer mp;
    private Vibrator v;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationId = 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        displayNotification();
        alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createWaveform(new long[]{1000, 1000}, 0));
        startSound();
        return START_STICKY;
    }

    /**
     * Creates and displays the notification and popup view for the foreground service
     */
    private void displayNotification() {
        Intent notificationIntent = new Intent(this, AttentionActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.channelId))
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle("Attention Requested")
                        .setContentText("Fedora has requested your attention")
                        .setChannelId(getString(R.string.channelId))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setContentIntent(pendingIntent)
                        .setFullScreenIntent(pendingIntent, true);
        startForeground(notificationId++, notificationBuilder.build());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeDevice(pm);
        if (pm.isInteractive()) startActivity(notificationIntent);
    }

    /**
     * Wakes the device
     *
     * @param pm the PowerManager
     */
    private void wakeDevice(PowerManager pm) {
        PowerManager.WakeLock wake = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ATTENTION:");
        wake.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        v.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startSound() {
        mp = new MediaPlayer();
        AudioAttributes aa = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build();
        mp.setAudioAttributes(aa);
        mp.setLooping(true);
        try {
            mp.setDataSource(this, alarm);
            mp.prepare();
            mp.start();
        } catch (IOException ignored) {
        }
    }
}
