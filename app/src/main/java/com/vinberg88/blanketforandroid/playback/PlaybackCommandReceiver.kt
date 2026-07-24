package com.vinberg88.blanketforandroid.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class PlaybackCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PlaybackForegroundService.ACTION_TOGGLE) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, PlaybackForegroundService::class.java)
                    .setAction(PlaybackForegroundService.ACTION_TOGGLE)
            )
        }
    }
}
