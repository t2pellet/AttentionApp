package com.commodorethrawn.attentionapp.activity;

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.commodorethrawn.attentionapp.R
import com.commodorethrawn.attentionapp.service.MessagingService
import com.commodorethrawn.attentionapp.util.ButtonUtil
import com.commodorethrawn.attentionapp.util.DatabaseUtil
import com.commodorethrawn.attentionapp.util.PreferenceUtil
import com.google.firebase.functions.FirebaseFunctions
import java.util.*

class FirstLaunchActivity : Activity() {

    private lateinit var functions : FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init vars
        functions = FirebaseFunctions.getInstance()
        // Setup content
        setContentView(R.layout.activity_setup_name)
        // Button listener
        val btnConfirmName : Button = findViewById(R.id.btnConfirmName)
        btnConfirmName.setOnClickListener(nameListener)
    }

    private val nameListener = View.OnClickListener {
        // Animate
        ButtonUtil.doPressAnimation(applicationContext, it as Button)
        // Set data
        val name = findViewById<EditText>(R.id.editName)
        PreferenceUtil.name = name.text.toString()
        MessagingService.generateToken(applicationContext)
        // Display new content
        setContentView(R.layout.activity_setup_choose)
        val btnCreate = findViewById<Button>(R.id.btnGet)
        val btnJoin = findViewById<Button>(R.id.btnGive)
        // Button listeners
        btnJoin.setOnClickListener(joinListener)
        btnCreate.setOnClickListener(createListener)
    }

    private val joinListener = View.OnClickListener {
        // Animate
        ButtonUtil.doPressAnimation(applicationContext, it as Button)
        // Set data
        PreferenceUtil.role = PreferenceUtil.Role.RECEIVER
        // Display new content
        setContentView(R.layout.activity_setup_getter)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        // Button listener
        btnConfirm.setOnClickListener(confirmJoinListener)
    }

    private val createListener = View.OnClickListener {
        // Animate
        ButtonUtil.doPressAnimation(applicationContext, it as Button)
        // Set data
        val coupleId = UUID.randomUUID().toString().substring(0, 8)
        PreferenceUtil.coupleId = coupleId
        PreferenceUtil.role = PreferenceUtil.Role.SENDER
        DatabaseUtil.addToDB()
        // Display content
        setContentView(R.layout.activity_setup_giver)
        val text = findViewById<TextView>(R.id.code)
        text.text = coupleId
    }

    private val confirmJoinListener = View.OnClickListener {
        val text = findViewById<EditText>(R.id.editCode)
        val coupleId = text.text.toString().trim()
        DatabaseUtil.coupleExists(coupleId).continueWith { existsTask ->
            if (existsTask.result)  {
                // Animate
                ButtonUtil.doPressAnimation(applicationContext, it as Button)
                // Set data
                PreferenceUtil.coupleId = coupleId
                PreferenceUtil.setupComplete = true
                DatabaseUtil.addToDB()
                // Close
                finishAndRemoveTask()
            }  else {
                // Animate
                ButtonUtil.doErrorAnimation(applicationContext, it as Button)
            }
        }
    }

}
