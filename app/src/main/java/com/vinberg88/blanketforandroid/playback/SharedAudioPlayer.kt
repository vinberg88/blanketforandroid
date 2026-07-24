package com.vinberg88.blanketforandroid.playback

import android.content.Context
import com.vinberg88.blanketforandroid.audio.AudioPlayer

object SharedAudioPlayer {
    @Volatile
    private var instance: AudioPlayer? = null

    fun get(context: Context): AudioPlayer {
        return instance ?: synchronized(this) {
            instance ?: AudioPlayer(context.applicationContext).also { instance = it }
        }
    }
}
