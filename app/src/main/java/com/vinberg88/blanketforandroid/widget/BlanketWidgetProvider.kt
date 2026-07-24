package com.vinberg88.blanketforandroid.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.vinberg88.blanketforandroid.R
import com.vinberg88.blanketforandroid.playback.PlaybackCommandReceiver
import com.vinberg88.blanketforandroid.playback.PlaybackForegroundService

class BlanketWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val toggle = PendingIntent.getBroadcast(context, id,
                Intent(context, PlaybackCommandReceiver::class.java).setAction(PlaybackForegroundService.ACTION_TOGGLE),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val views = RemoteViews(context.packageName, R.layout.blanket_widget).apply {
                setOnClickPendingIntent(R.id.widget_play_pause, toggle)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
