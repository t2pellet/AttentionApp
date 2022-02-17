package com.commodorethrawn.attentionapp.service;

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.activity.AttentionActivity
import java.io.IOException

class AttentionService : Service() {

    private lateinit var alarm : Uri
    private lateinit var mp : MediaPlayer
    private lateinit var v : Vibrator
    private lateinit var pm : PowerManager

    override fun onCreate() {
        alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        mp = MediaPlayer()
        v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        pm = getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        displayNotification()
        alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(1000, 1000), 0))
        startSound()
        return START_STICKY
    }

    override fun onDestroy() {
        mp.stop()
        mp.release()
        v.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * Creates and displays the notification and popup view for the foreground service
     */
    private fun displayNotification() {
        val notificationIntent = Intent(this, AttentionActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val partnerName = getSharedPreferences("attentionapp", MODE_PRIVATE).getString("partnerName", "")
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.channelId))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentText("$partnerName has requested your attention")
                .setChannelId(getString(R.string.channelId))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
        startForeground(++MessagingService.notificationId, notificationBuilder.build())
        wakeDevice()
        startActivity(notificationIntent)
    }

    /**
     * Wakes the device
     * @param pm the PowerManager
     */
    private fun wakeDevice() {
        val wake = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "ATTENTION:")
        wake.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    /**
     * Starts playing the alarm sound on the device
     */
    private fun startSound() {
        val aa = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build()
        mp.setAudioAttributes(aa)
        mp.isLooping = true
        try {
            mp.setDataSource(this, alarm)
            mp.prepare()
            mp.start()
        } catch (ignored :  IOException) {
        }
    }
}
