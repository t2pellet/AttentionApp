package com.commodorethrawn.attentionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstLaunchActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Button fedoraButton;
    private Button tenzinButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        System.out.println("LAUNCHING");
        preferences = getSharedPreferences("com.commodorethrawn.attentionapp", MODE_PRIVATE);
        fedoraButton = findViewById(R.id.btnFedora);
        tenzinButton = findViewById(R.id.btnTenzin);
        fedoraButton.setOnClickListener(v -> {
            preferences.edit().putBoolean("isSender", true).apply();
            startActivity(new Intent(this, MainActivity.class));
        });
        tenzinButton.setOnClickListener(v -> {
            preferences.edit().putBoolean("isSender", false).apply();
            finishAndRemoveTask();
        });
        MessagingService.updateToken(this);
    }
}
