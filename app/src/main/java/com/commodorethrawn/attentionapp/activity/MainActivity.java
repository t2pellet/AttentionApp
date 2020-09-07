package com.commodorethrawn.attentionapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.util.ButtonUtil;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private FirebaseFunctions functions;
    private Button attentionButton;
    private TextView feedbackText;
    private long lastClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        functions = FirebaseFunctions.getInstance();
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        feedbackText = findViewById(R.id.feedbackText);
        attentionButton = findViewById(R.id.attention_button);
        attentionButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isFirstLaunch = !preferences.getBoolean("isSetup", false);
        if (isFirstLaunch) {
            Intent intent = new Intent(MainActivity.this, FirstLaunchActivity.class);
            startActivity(intent);
        } else if (preferences.getBoolean("isBoyfriend", false)) {
            finishAndRemoveTask();
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick > 8000) {
            ButtonUtil.doPressAnimation(this, attentionButton);
            requestAttention();
            lastClick = System.currentTimeMillis();
        } else {
            ButtonUtil.doErrorAnimation(this, attentionButton);
        }
    }

    private void requestAttention() {
        String coupleId = preferences.getString("coupleId", "");
        if (coupleId != null && !coupleId.isEmpty()) {
            functions.getHttpsCallable("requestAttention")
                    .call(coupleId)
                    .continueWith(task -> {
                        String result = (String) Objects.requireNonNull(task.getResult()).getData();
                        displayResult(result);
                        return result;
                    });
        }
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