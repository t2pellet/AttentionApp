package com.commodorethrawn.attentionapp.activity;

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.util.ButtonUtil
import com.commodorethrawn.attentionapp.util.PreferenceUtil
import com.google.firebase.functions.FirebaseFunctions
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : Activity(), View.OnClickListener {

    private lateinit var functions : FirebaseFunctions
    private lateinit var attentionButton : Button
    private lateinit var feedbackText : TextView
    private var lastClick : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init vars
        PreferenceUtil.init(this)
        functions = FirebaseFunctions.getInstance()
        // Set content
        setContentView(R.layout.activity_main)
        feedbackText = findViewById(R.id.feedbackText)
        attentionButton = findViewById(R.id.attention_button)
        //  Button listener
        attentionButton.setOnClickListener(this);
    }

    override fun onResume() {
        super.onResume()
        // Start setup activity if needed
        if (!PreferenceUtil.setupComplete) {
            val intent = Intent(this, FirstLaunchActivity::class.java)
            startActivity(intent)
        } // If we're the receiver, we shouldn't be able to open the app, so we close it!
        else if (PreferenceUtil.role == PreferenceUtil.Role.RECEIVER) {
            finishAndRemoveTask()
        }
    }

    override fun onClick(button: View?) {
        // Limit how often they can do it. Firebase is a bit slow when the app has no userbase, but we still don't want spamming :)
        if (lastClick < 0 || System.currentTimeMillis() - lastClick > 8000) {
            ButtonUtil.doPressAnimation(this, attentionButton);
            requestAttention();
            lastClick = System.currentTimeMillis();
        } else {
            ButtonUtil.doErrorAnimation(this, attentionButton);
        }
    }

    private fun requestAttention() {
        val coupleId = PreferenceUtil.coupleId;
        if (coupleId.isNotEmpty()) {
            functions.getHttpsCallable("request").call(coupleId)
                    .continueWith {
                        val result = it.result?.data as String?
                        displayResult(result)
                    }
        }
    }

    private fun displayResult(result : String?) {
        feedbackText.text = result
        val timerReset = Timer()
        timerReset.schedule(3000) {
            feedbackText.text = ""
        }
    }
}