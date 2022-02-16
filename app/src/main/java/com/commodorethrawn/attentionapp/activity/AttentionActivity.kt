package com.commodorethrawn.attentionapp.activity;

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.service.AttentionService
import com.google.firebase.functions.FirebaseFunctions
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AttentionActivity : Activity(), View.OnClickListener {

    private lateinit var functions : FirebaseFunctions
    private lateinit var preferences : SharedPreferences
    private lateinit var attentionText : TextView
    private lateinit var t : Timer

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
        functions = FirebaseFunctions.getInstance()
        preferences = getSharedPreferences("attentionapp", MODE_PRIVATE)
        attentionText = findViewById(R.id.attentionText)
        val parterName = preferences.getString("parterName", "")?.uppercase()
        val attentionTextValue = parterName + " WANTS ATTENTION"
        t = Timer()
        findViewById<Button>(R.id.acknowledge).setOnClickListener(this)
        t.scheduleAtFixedRate(0, 1000) {
            if (attentionText.text == "") {
                attentionText.text = attentionTextValue
            } else attentionText.text = ""
        }
    }

    /**
     * Sets up the activity to display the appropriate view, with appropriate parameters/flags
     */
    private fun setupWindow() {
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

    override fun onClick(view: View?) {
        val coupleId = preferences.getString("coupleId", "");
        functions.getHttpsCallable("acknowledge").call(coupleId);
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll();
        t.cancel();
        t.purge();
        stopService(Intent(this, AttentionService::class.java))
        finishAndRemoveTask();
    }

    override fun onBackPressed() {
        // Ignore to make closing the activity more inconvenient
    }

}
