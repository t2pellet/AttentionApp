package com.commodorethrawn.attentionapp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.service.AttentionService;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFunctions functions;
    private SharedPreferences preferences;
    private TextView attentionText;
    private Timer t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        functions = FirebaseFunctions.getInstance();
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        attentionText = findViewById(R.id.attentionText);
        String parterName = preferences.getString("parterName", "").toUpperCase();
        String attentionTextValue = parterName + " WANTS ATTENTION";
        t = new Timer();
        findViewById(R.id.acknowledge).setOnClickListener(this);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (attentionText.getText().equals(""))
                    attentionText.setText(attentionTextValue);
                else
                    attentionText.setText("");
            }
        }, 0, 1000);
    }

    /**
     * Sets up the activity to display the appropriate view, with appropriate parameters/flags
     */
    private void setupWindow() {
        setContentView(R.layout.activity_attention);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } else {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
    }

    @Override
    public void onClick(View view) {
        String coupleId = preferences.getString("coupleId", "");
        functions.getHttpsCallable("acknowledge").call(coupleId);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        t.cancel();
        t.purge();
        stopService(new Intent(this, AttentionService.class));
        finishAndRemoveTask();
    }

    @Override
    public void onBackPressed() {
        // I'm just trying to make it more inconvenient for me to close the activity
    }

}
