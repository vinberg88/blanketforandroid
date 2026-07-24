package com.vinberg88.blanketforandroid.playback

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

object PlaybackController {
    fun startPlaybackService(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, PlaybackForegroundService::class.java).setAction(PlaybackForegroundService.ACTION_START)
        )
    }

    fun togglePlayback(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, PlaybackForegroundService::class.java).setAction(PlaybackForegroundService.ACTION_TOGGLE)
        )
    }

    fun stopPlaybackService(context: Context) {
        context.stopService(Intent(context, PlaybackForegroundService::class.java))
    }
}
