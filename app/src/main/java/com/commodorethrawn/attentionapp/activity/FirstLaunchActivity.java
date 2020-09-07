package com.commodorethrawn.attentionapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.commodorethrawn.attentionapp.R;
import com.commodorethrawn.attentionapp.service.MessagingService;
import com.commodorethrawn.attentionapp.util.ButtonUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.UUID;

public class FirstLaunchActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_name);
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
        Button btnConfirmName = findViewById(R.id.btnConfirmName);
        btnConfirmName.setOnClickListener(new NameListener());
    }

    private class NameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ButtonUtil.doPressAnimation(getApplicationContext(), (Button) v);
            EditText name = findViewById(R.id.editName);
            preferences.edit().putString("name", name.getText().toString()).apply();
            setContentView(R.layout.activity_first);
            Button btnCreate = findViewById(R.id.btnGet);
            Button btnJoin = findViewById(R.id.btnGive);
            MessagingService.generateToken(getApplicationContext());
            btnJoin.setOnClickListener(new JoinListener());
            btnCreate.setOnClickListener(new CreateListener());
        }
    }

    private class JoinListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            setContentView(R.layout.activity_setup_getter);
            preferences.edit()
                    .putBoolean("isBoyfriend", true)
                    .apply();
            String token = preferences.getString("token", "");
            String name = preferences.getString("name", "");
            findViewById(R.id.btnConfirm).setOnClickListener(button -> {
                EditText text = findViewById(R.id.editCode);
                String coupleId = text.getText().toString();
                database.getReference(coupleId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    ButtonUtil.doPressAnimation(getApplicationContext(), (Button) button);
                                    preferences.edit()
                                            .putString("coupleId", coupleId)
                                            .putBoolean("isSetup", true)
                                            .apply();
                                    database.getReference(coupleId).child("boyfriend").setValue(token);
                                    database.getReference(coupleId).child("boyfriendName").setValue(name);
                                    FirebaseFunctions.getInstance().getHttpsCallable("activate").call(coupleId);
                                    finishAndRemoveTask();
                                } else
                                    ButtonUtil.doErrorAnimation(getApplicationContext(), (Button) button);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }
                );
            });
        }
    }

    private class CreateListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String coupleId = UUID.randomUUID().toString().substring(0, 8);
            setContentView(R.layout.activity_setup_giver);
            TextView tv = findViewById(R.id.code);
            tv.setText(coupleId);
            preferences.edit()
                    .putString("coupleId", coupleId)
                    .putBoolean("isBoyfriend", false)
                    .apply();
            String token = preferences.getString("token", "");
            database.getReference(coupleId).child("girlfriend").setValue(token);
            String name = preferences.getString("name", "");
            database.getReference(coupleId).child("girlfriendName").setValue(name);
        }
    }

}
