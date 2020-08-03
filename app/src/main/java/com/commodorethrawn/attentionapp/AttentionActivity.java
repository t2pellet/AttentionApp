package com.commodorethrawn.attentionapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.functions.FirebaseFunctions;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFunctions functions;
    private TextView attentionText;
    private Uri alarm;
    private MediaPlayer mp;
    private Vibrator v;
    private Timer t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_attention);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        functions = FirebaseFunctions.getInstance();
        alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createWaveform(new long[]{1000,1000},0));
        mp = MediaPlayer.create(this, alarm);
        mp.setLooping(true);
        mp.start();
        t = new Timer();
        attentionText = findViewById(R.id.attentionText);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (attentionText.getText().equals(""))
                    attentionText.setText(getString(R.string.attention_requested));
                else
                    attentionText.setText("");
            }
        }, 0, 1000);
        findViewById(R.id.acknowledge).setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        NotificationManagerCompat.from(this).cancel(0);
        functions.getHttpsCallable("acknowledge").call();
        mp.stop();
        mp.release();
        v.cancel();
        t.cancel();
        t.purge();
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
