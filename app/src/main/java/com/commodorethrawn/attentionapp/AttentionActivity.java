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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.functions.FirebaseFunctions;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener {

    private Button acknowledgeButton;
    private TextView attentionText;
    private Timer t;
    private MediaPlayer mp;
    private Vibrator v;
    private FirebaseFunctions functions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        functions = FirebaseFunctions.getInstance();
        Uri alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(this, alarm);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp.setLooping(true);
        mp.start();
        v.vibrate(VibrationEffect.createWaveform(new long[]{1000,1000},0));
        acknowledgeButton = findViewById(R.id.acknowledge);
        acknowledgeButton.setOnClickListener(this);
        attentionText = findViewById(R.id.attentionText);
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (attentionText.getText().equals(""))
                    attentionText.setText(getString(R.string.attention_requested));
                else
                    attentionText.setText("");
            }
        }, 0, 1000);
    }


    @Override
    public void onClick(View v) {
        NotificationManagerCompat.from(this).cancel(0);
        functions.getHttpsCallable("acknowledge").call();
        finishAndRemoveTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
        v.cancel();
        t.cancel();
    }
}
