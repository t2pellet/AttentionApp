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

import java.util.Timer;
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var preferences : SharedPreferences
    private lateinit var functions : FirebaseFunctions
    private lateinit var attentionButton : Button
    private lateinit var feedbackText : TextView
    private var lastClick : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        functions = FirebaseFunctions.getInstance()
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE)
        feedbackText = findViewById(R.id.feedbackText)
        attentionButton = findViewById(R.id.attention_button)
        attentionButton.setOnClickListener(this);
    }

    override fun onResume() {
        super.onResume()
        val isFirstLaunch = !preferences.getBoolean("isSetup", false)
        if (isFirstLaunch) {
            val intent = Intent(this, FirstLaunchActivity::class.java)
            startActivity(intent)
        } else if (preferences.getBoolean("isBoyfriend", false)) {
            finishAndRemoveTask()
        }
    }

    override fun onClick(p0: View?) {
        if (lastClick < 0 || System.currentTimeMillis() - lastClick > 8000) {
            ButtonUtil.doPressAnimation(this, attentionButton);
            requestAttention();
            lastClick = System.currentTimeMillis();
        } else {
            ButtonUtil.doErrorAnimation(this, attentionButton);
        }
    }

    private fun requestAttention() {
        val coupleId = preferences.getString("coupleId", "")
        if (coupleId != null && !coupleId.isEmpty()) {
            functions.getHttpsCallable("requestAttention")
                    .call(coupleId)
                    .continueWith {
                        val result = it.result?.data as String?
                        displayResult(result)
                    }
        }
    }

    private fun displayResult(result : String?) {
        feedbackText.setText(result)
        val timerReset = Timer()
        timerReset.schedule(3000) {
            feedbackText.text = ""
        }
    }
}