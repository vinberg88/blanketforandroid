package com.vinberg88.blanketforandroid.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaybackCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PlaybackForegroundService.ACTION_TOGGLE) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                PlaybackController.togglePlayback(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
