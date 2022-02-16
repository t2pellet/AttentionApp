package com.commodorethrawn.attentionapp.util;

import android.content.Context;
import android.content.Context.VIBRATOR_SERVICE
import android.graphics.LightingColorFilter;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import com.commodorethrawn.attentionapp.R;

import java.util.Timer;
import kotlin.concurrent.schedule

object ButtonUtil {

    private val buttonPressFilter = LightingColorFilter(0xFF7F7F7F.toInt(), 0x00000000)
    private val buttonTimer = Timer()

    fun doPressAnimation(ctx : Context, button : Button) {
        val v = ctx.getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        button.background.colorFilter = buttonPressFilter
        buttonTimer.schedule(100) {
            button.background.colorFilter = null
        }
    }

    fun doErrorAnimation(ctx : Context, button : Button) {
        button.background = ctx.getDrawable(R.drawable.my_btn_err);
        buttonTimer.schedule(1000) {
            button.background = ctx.getDrawable(R.drawable.my_btn)
        }
    }
}
