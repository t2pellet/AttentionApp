package com.commodorethrawn.attentionapp.activity;

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.service.AttentionService
import com.commodorethrawn.attentionapp.util.PreferenceUtil
import com.google.firebase.functions.FirebaseFunctions
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AttentionActivity : Activity(), View.OnClickListener {

    private lateinit var functions : FirebaseFunctions
    private lateinit var attentionText : TextView
    private lateinit var t : Timer

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        // Init vars
        functions = FirebaseFunctions.getInstance()
        attentionText = findViewById(R.id.attentionText)
        t = Timer()
        // Setup window
        setupWindow()
        // Start alert
        startAlert()
        // Button click
        findViewById<Button>(R.id.acknowledge).setOnClickListener(this)
    }

    /**
     * Sets up the activity to display the appropriate view, with appropriate parameters/flags
     */
    private fun setupWindow() {
        setContentView(R.layout.activity_attention)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    }

    /**
     * Starts alert (vibration and popup text)
     */
    private fun startAlert() {
        val attentionTextValue = "${PreferenceUtil.parterName.uppercase()} WANTS ATTENTION"
        t.scheduleAtFixedRate(0, 1000) {
            if (attentionText.text == "") {
                attentionText.text = attentionTextValue
            } else attentionText.text = ""
        }
    }

    override fun onClick(view: View?) {
        functions.getHttpsCallable("respond").call(PreferenceUtil.coupleId)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
        t.cancel()
        t.purge()
        stopService(Intent(this, AttentionService::class.java))
        finishAndRemoveTask()
    }

    override fun onBackPressed() {
        // Ignore to make closing the activity more inconvenient
    }

}
