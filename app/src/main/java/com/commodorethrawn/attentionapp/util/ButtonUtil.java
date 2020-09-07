package com.commodorethrawn.attentionapp.util;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import com.commodorethrawn.attentionapp.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.VIBRATOR_SERVICE;

public class ButtonUtil {

    private static LightingColorFilter buttonPressFilter = new LightingColorFilter(0xFF7F7F7F,
            0x00000000);
    private static Timer buttonTimer = new Timer();

    public static void doPressAnimation(Context ctx, Button button) {
        Vibrator v = (Vibrator) ctx.getSystemService(VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        button.getBackground().setColorFilter(buttonPressFilter);
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                button.getBackground().setColorFilter(null);
            }
        }, 100);
    }

    public static void doErrorAnimation(Context ctx, Button button) {
        button.setBackground(ctx.getDrawable(R.drawable.my_btn_err));
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                button.setBackground(ctx.getDrawable(R.drawable.my_btn));
            }
        }, 1000);
    }
}
