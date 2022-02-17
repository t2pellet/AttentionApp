package com.commodorethrawn.attentionapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object PreferenceUtil {

    enum class Role {
        SENDER,
        RECEIVER
    }

    private lateinit var preferences : SharedPreferences

    fun init(ctx : Context) {
        preferences = ctx.getSharedPreferences("attentionapp", MODE_PRIVATE)
    }

    var setupComplete : Boolean
        get() = preferences.getBoolean("isSetup", false)
        set(value) = preferences.edit().putBoolean("isSetup",  value).apply()

    var name : String
        get() = preferences.getString("name", "")!!
        set(value) = preferences.edit().putString("name", value).apply()

    var token : String
        get() = preferences.getString("token", "")!!
        set(value) = preferences.edit().putString("token", value).apply()

    var role : Role
        get() = Role.valueOf(preferences.getString("role", Role.SENDER.name)!!)
        set(value) = preferences.edit().putString("role", value.name).apply()

    var coupleId : String
        get() = preferences.getString("coupleId", "")!!
        set(value) = preferences.edit().putString("coupleId", value).apply()

    var parterName : String
        get() = preferences.getString("partnerName", "")!!
        set(value) = preferences.edit().putString("partnerName", value).apply()

}