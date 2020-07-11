package com.commodorethrawn.attentionapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirstLaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private Button btnFedora;
    private Button btnTenzin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FirebaseMessaging.getInstance().subscribeToTopic("girlfriend");
        preferences = getSharedPreferences("com.commodorethrawn.attentionapp", MODE_PRIVATE);
        btnFedora = findViewById(R.id.btnFedora);
        btnTenzin = findViewById(R.id.btnTenzin);
        btnFedora.setOnClickListener(this);
        btnTenzin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnFedora) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            findViewById(R.id.identifyText).setVisibility(View.INVISIBLE);
            btnFedora.setVisibility(View.INVISIBLE);
            btnTenzin.setVisibility(View.INVISIBLE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("girlfriend");
            FirebaseMessaging.getInstance().subscribeToTopic("boyfriend");
            preferences.edit().putBoolean("isSender", false).apply();
            preferences.edit().putBoolean("isFirstLaunch", false).apply();
            FirebaseFunctions.getInstance().getHttpsCallable("updateBoyfriend").call();
            finishAndRemoveTask();
        }
    }
}
