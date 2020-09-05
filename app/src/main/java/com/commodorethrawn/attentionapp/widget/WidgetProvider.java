package com.commodorethrawn.attentionapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.RemoteViews;

import com.commodorethrawn.attentionapp.R;
import com.google.firebase.functions.FirebaseFunctions;

public class WidgetProvider extends AppWidgetProvider {

    private static long lastClick = System.currentTimeMillis();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int currentId : appWidgetIds) {
            Intent intent = new Intent(context, getClass());
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra("requestingAttention", true);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widgetButton, PendingIntent.getBroadcast(context, 0, intent, 0));
            appWidgetManager.updateAppWidget(currentId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.hasExtra("requestingAttention")) {
            if (System.currentTimeMillis() - lastClick > 10000) {
                FirebaseFunctions.getInstance().getHttpsCallable("requestAttention").call();
                lastClick = System.currentTimeMillis();
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }
}
