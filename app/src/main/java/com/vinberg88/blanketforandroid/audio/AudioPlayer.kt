package com.vinberg88.blanketforandroid.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.vinberg88.blanketforandroid.model.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class AudioPlayer(private val context: Context) {
    private val players: MutableMap<String, MediaPlayer> = ConcurrentHashMap()

    suspend fun loadSound(sound: Sound) {
        withContext(Dispatchers.IO) {
            try {
                if (!players.containsKey(sound.id)) {
                    val afd: AssetFileDescriptor = context.assets.openFd("sounds/${sound.fileName}")
                    val mediaPlayer = MediaPlayer().apply {
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        isLooping = true
                        prepare()
                    }
                    afd.close()
                    players[sound.id] = mediaPlayer
                    Log.d(TAG, "Successfully loaded sound: ${sound.id}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load sound: ${sound.id}, file: ${sound.fileName}", e)
            }

            Unit
        }
    }

    suspend fun loadSoundFromUri(sound: Sound, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                if (!players.containsKey(sound.id)) {
                    context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                        val mediaPlayer = MediaPlayer().apply {
                            setDataSource(pfd.fileDescriptor)
                            isLooping = true
                            prepare()
                        }
                        players[sound.id] = mediaPlayer
                        Log.d(TAG, "Successfully loaded custom sound: ${sound.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load custom sound: ${sound.id}", e)
            }

            Unit
        }
    }

    fun releaseSound(soundId: String) {
        players[soundId]?.let { player ->
            if (player.isPlaying) player.pause()
            player.release()
            players.remove(soundId)
            Log.d(TAG, "Released sound: $soundId")
        }
    }

    fun play(soundId: String) {
        players[soundId]?.let { player ->
            if (!player.isPlaying) {
                player.start()
            }
        }
    }

    fun pause(soundId: String) {
        players[soundId]?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    fun setVolume(soundId: String, volume: Float) {
        players[soundId]?.setVolume(volume, volume)
    }

    fun pauseAll() {
        players.values.forEach { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    fun resumeAll(enabledSounds: Set<String>) {
        enabledSounds.forEach { soundId ->
            players[soundId]?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                }
            }
        }
    }

    fun release() {
        players.values.forEach { it.release() }
        players.clear()
    }

    companion object {
        private const val TAG = "AudioPlayer"
    }
}
