package com.commodorethrawn.attentionapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.functions.FirebaseFunctions;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFunctions functions;
    private TextView attentionText;
    private Timer t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } else {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        functions = FirebaseFunctions.getInstance();
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
        findViewById(R.id.acknowledge).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        functions.getHttpsCallable("acknowledge").call();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        t.cancel();
        t.purge();
        stopService(new Intent(this, AttentionService.class));
        finishAndRemoveTask();
    }

    @Override
    public void onBackPressed() {
    }

}
