package com.commodorethrawn.attentionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private FirebaseFunctions functions;
    private Button attentionButton;
    private TextView feedbackText;
    private long lastClick;
    private static LightingColorFilter buttonPressFilter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        functions = FirebaseFunctions.getInstance();
        preferences = getSharedPreferences("com.commodorethrawn.attentionapp", MODE_PRIVATE);
        feedbackText = findViewById(R.id.textView);
        attentionButton = findViewById(R.id.attention_button);
        attentionButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            preferences.edit().putBoolean("isFirstLaunch", false).apply();
            Intent intent = new Intent(MainActivity.this, FirstLaunchActivity.class);
            startActivity(intent);
        } else {
            boolean isReceiver = !preferences.getBoolean("isSender", false);
            if (isReceiver) finishAndRemoveTask();
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick > 10000) {
            requestAttention();
            lastClick = System.currentTimeMillis();
            doClickAnimation(attentionButton);
        } else {
            attentionButton.setBackground(getDrawable(R.drawable.my_btn_err));
            Timer timerReset = new Timer();
            timerReset.schedule(new TimerTask() {
                @Override
                public void run() {
                    attentionButton.setBackground(getDrawable(R.drawable.my_btn));
                }
            }, 1000);
        }
    }
    private void doClickAnimation(Button button) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
        button.getBackground().setColorFilter(buttonPressFilter);
        Timer timerReset = new Timer();
        timerReset.schedule(new TimerTask() {
            @Override
            public void run() {
                attentionButton.getBackground().setColorFilter(null);
            }
        }, 100);
    }

    private Task<String> requestAttention() {
        return functions
                .getHttpsCallable("requestAttention")
                .call()
                .continueWith(task -> {
                    String result = (String) task.getResult().getData();
                    displayResult(result);
                    return result;
                });
    }

    private void displayResult(String result) {
        feedbackText.setText(result);
        Timer timerReset = new Timer();
        timerReset.schedule(new TimerTask() {
            @Override
            public void run() {
                feedbackText.setText("");
            }
        }, 3000);
    }

}