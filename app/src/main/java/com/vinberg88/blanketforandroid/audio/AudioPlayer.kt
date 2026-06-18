package com.vinberg88.blanketforandroid.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.vinberg88.blanketforandroid.model.Sound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class AudioPlayer(private val context: Context) {
    private val players: MutableMap<String, MediaPlayer> = ConcurrentHashMap()
    private val soundVolumes: MutableMap<String, Float> = ConcurrentHashMap()
    private val fadeJobs: MutableMap<String, Job> = ConcurrentHashMap()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var masterVolume: Float = 1f

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
        fadeJobs.remove(soundId)?.cancel()
        players[soundId]?.let { player ->
            if (player.isPlaying) player.pause()
            player.release()
            players.remove(soundId)
            soundVolumes.remove(soundId)
            Log.d(TAG, "Released sound: $soundId")
        }
    }

    fun play(soundId: String) {
        players[soundId]?.let { player ->
            if (!player.isPlaying) {
                player.setVolume(0f, 0f)
                player.start()
            }
            fadeTo(soundId, effectiveVolume(soundId), startVolume = 0f)
        }
    }

    fun pause(soundId: String) {
        players[soundId]?.let { player ->
            if (player.isPlaying) {
                fadeTo(soundId, 0f, startVolume = effectiveVolume(soundId)) {
                    if (player.isPlaying) player.pause()
                }
            }
        }
    }

    fun setVolume(soundId: String, volume: Float) {
        soundVolumes[soundId] = volume.coerceIn(0f, 1f)
        players[soundId]?.setVolume(effectiveVolume(soundId), effectiveVolume(soundId))
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        players.forEach { (soundId, player) ->
            val volumeForSound = effectiveVolume(soundId)
            player.setVolume(volumeForSound, volumeForSound)
        }
    }

    fun pauseAll() {
        players.forEach { (soundId, player) ->
            if (player.isPlaying) {
                fadeTo(soundId, 0f, startVolume = effectiveVolume(soundId)) {
                    if (player.isPlaying) player.pause()
                }
            }
        }
    }

    fun resumeAll(enabledSounds: Set<String>) {
        enabledSounds.forEach { soundId ->
            players[soundId]?.let { player ->
                if (!player.isPlaying) {
                    player.setVolume(0f, 0f)
                    player.start()
                }
                fadeTo(soundId, effectiveVolume(soundId), startVolume = 0f)
            }
        }
    }

    fun release() {
        fadeJobs.values.forEach { it.cancel() }
        fadeJobs.clear()
        players.values.forEach { it.release() }
        players.clear()
        soundVolumes.clear()
    }

    private fun effectiveVolume(soundId: String): Float {
        return (soundVolumes[soundId] ?: 0.5f) * masterVolume
    }

    private fun fadeTo(
        soundId: String,
        targetVolume: Float,
        startVolume: Float,
        onComplete: (() -> Unit)? = null
    ) {
        fadeJobs.remove(soundId)?.cancel()
        val player = players[soundId] ?: return
        val steps = 12
        val durationMs = 450L

        fadeJobs[soundId] = scope.launch {
            repeat(steps) { index ->
                val progress = (index + 1).toFloat() / steps
                val nextVolume = startVolume + (targetVolume - startVolume) * progress
                player.setVolume(nextVolume, nextVolume)
                delay(durationMs / steps)
            }
            player.setVolume(targetVolume, targetVolume)
            fadeJobs.remove(soundId)
            onComplete?.invoke()
        }
    }

    companion object {
        private const val TAG = "AudioPlayer"
    }
}
