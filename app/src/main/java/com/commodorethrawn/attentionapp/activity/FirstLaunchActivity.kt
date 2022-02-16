package com.commodorethrawn.attentionapp.activity;

import android.app.Activity
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

class FirstLaunchActivity : Activity() {

    private lateinit var preferences : SharedPreferences
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_name);
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance()
        val btnConfirmName : Button = findViewById(R.id.btnConfirmName)
        btnConfirmName.setOnClickListener(nameListener)
    }

    private val nameListener = View.OnClickListener {
        ButtonUtil.doPressAnimation(applicationContext, it as Button)
        val name = findViewById<EditText>(R.id.editName)
        preferences.edit().putString("name", name.text.toString()).apply()
        setContentView(R.layout.activity_first)
        val btnCreate = findViewById<Button>(R.id.btnGet)
        val btnJoin = findViewById<Button>(R.id.btnGive)
        MessagingService.generateToken(applicationContext)
        btnJoin.setOnClickListener(joinListener)
        btnCreate.setOnClickListener(createListener)
    }

    private val joinListener = View.OnClickListener {
        setContentView(R.layout.activity_setup_getter)
        preferences.edit().putBoolean("isBoyfriend", true).apply()
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        btnConfirm.setOnClickListener(confirmJoinListener)
    }

    private val createListener = View.OnClickListener {
        val coupleId = UUID.randomUUID().toString().substring(0, 8)
        setContentView(R.layout.activity_setup_giver)
        val text = findViewById<TextView>(R.id.code)
        text.text = coupleId
        preferences.edit()
                .putString("coupleId", coupleId)
                .putBoolean("isBoyfriend", false)
                .apply()
        val token = preferences.getString("token", "")
        database.getReference(coupleId).child("girlfriend").setValue(token)
        val name = preferences.getString("name", "")
        database.getReference(coupleId).child("girlfriendName").setValue(name)
    }

    private val confirmJoinListener = View.OnClickListener {
        val token = preferences.getString("token", "")
        val name = preferences.getString("name", "")
        val text = findViewById<EditText>(R.id.editCode)
        val coupleId = text.text.toString()
        database.getReference(coupleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    ButtonUtil.doPressAnimation(applicationContext, it as Button)
                    preferences.edit()
                            .putString("coupleId", coupleId)
                            .putBoolean("isSetup", true)
                            .apply()
                    database.getReference(coupleId).child("boyfriend").setValue(token)
                    database.getReference(coupleId).child("boyfriendName").setValue(name)
                    FirebaseFunctions.getInstance().getHttpsCallable("activate").call(coupleId)
                    finishAndRemoveTask()
                } else {
                    ButtonUtil.doErrorAnimation(applicationContext, it as Button)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}
