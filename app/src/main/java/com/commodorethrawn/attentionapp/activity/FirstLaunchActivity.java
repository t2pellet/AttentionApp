package com.commodorethrawn.attentionapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.service.MessagingService;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirstLaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private Button btnFedora;
    private Button btnTenzin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FirebaseMessaging.getInstance().subscribeToTopic("setup");
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        btnFedora = findViewById(R.id.btnFedora);
        btnTenzin = findViewById(R.id.btnTenzin);
        btnFedora.setOnClickListener(this);
        btnTenzin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MessagingService.generateToken(this);
        if (preferences.contains("token")) {
            String token = preferences.getString("token", "");
            preferences.edit().putString("token", token).apply();
            if (v == btnFedora) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.identifyText).setVisibility(View.INVISIBLE);
                btnFedora.setVisibility(View.INVISIBLE);
                btnTenzin.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference("girlfriend").setValue(token);
                preferences.edit().putBoolean("isBoyfriend", false).apply();
            } else {
                FirebaseDatabase.getInstance().getReference("boyfriend").setValue(token);
                preferences.edit().putBoolean("isBoyfriend", true).apply();
                preferences.edit().putBoolean("isSetup", true).apply();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("setup");
                finishAndRemoveTask();
            }
        }
    }
}
