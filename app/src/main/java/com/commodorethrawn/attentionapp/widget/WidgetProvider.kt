package com.commodorethrawn.attentionapp.widget;

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.RemoteViews

import com.commodorethrawn.attentionapp.R
import com.google.firebase.functions.FirebaseFunctions

import android.content.Context.MODE_PRIVATE;

class WidgetProvider : AppWidgetProvider() {

    private var lastCLick = System.currentTimeMillis()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        for (currentId in appWidgetIds!!) {
            val intent = Intent(context, javaClass)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra("requestingAttention", true)
            val views = RemoteViews(context!!.packageName, R.layout.widget)
            views.setOnClickPendingIntent(R.id.widgetButton, PendingIntent.getBroadcast(context, 0, intent, 0));
            appWidgetManager!!.updateAppWidget(currentId, views);
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.hasExtra("requestingAttention") && System.currentTimeMillis() - lastCLick > 5000) {
            val coupleId = context!!.getSharedPreferences("attentionapp", MODE_PRIVATE).getString("coupleId", "")
            if (coupleId != null && !coupleId.isEmpty()) {
                FirebaseFunctions.getInstance().getHttpsCallable("requestAttention").call(coupleId)
                lastCLick = System.currentTimeMillis()
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }
}
