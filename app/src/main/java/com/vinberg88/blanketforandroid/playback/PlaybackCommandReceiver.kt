package com.vinberg88.blanketforandroid.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PlaybackCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PlaybackForegroundService.ACTION_TOGGLE) return

        val pendingResult = goAsync()
        PlaybackController.togglePlaybackAsync(context) { pendingResult.finish() }
    }
}
