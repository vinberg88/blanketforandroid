package com.vinberg88.blanketforandroid.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vinberg88.blanketforandroid.MainActivity
import com.vinberg88.blanketforandroid.R
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.availableSounds
import com.vinberg88.blanketforandroid.model.iconForName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaybackForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var prefsRepository: PreferencesRepository
    private val loadedSoundIds = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        prefsRepository = PreferencesRepository(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            when (intent?.action) {
                ACTION_TOGGLE -> togglePlayback()
                ACTION_START, null -> startPlaybackFromPreferences()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun togglePlayback() {
        if (prefsRepository.isPlaying.first()) {
            SharedAudioPlayer.get(applicationContext).pauseAll()
            prefsRepository.setIsPlaying(false)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            startPlaybackFromPreferences()
        }
    }

    private suspend fun startPlaybackFromPreferences() {
        ensureNotificationForeground()
        val audioPlayer = SharedAudioPlayer.get(applicationContext)
        val customSounds = prefsRepository.customSounds.first().map {
            Sound(
                id = it.id,
                fileName = it.uriString,
                displayName = it.displayName,
                icon = iconForName(it.iconName),
                iconName = it.iconName,
                isCustom = true
            )
        }
        val sounds = availableSounds + customSounds

        sounds.forEach { sound ->
            if (loadedSoundIds.add(sound.id)) {
                if (sound.isCustom) {
                    audioPlayer.loadSoundFromUri(sound, Uri.parse(sound.fileName))
                } else {
                    audioPlayer.loadSound(sound)
                }
            }
        }

        audioPlayer.setMasterVolume(prefsRepository.masterVolume.first())
        sounds.forEach { sound ->
            val state = prefsRepository.getSoundState(sound.id).first()
            audioPlayer.setVolume(sound.id, state.volume)
            if (state.isEnabled) {
                audioPlayer.play(sound.id)
            }
        }
        prefsRepository.setIsPlaying(true)
    }

    private fun ensureNotificationForeground() {
        createChannel()
        val toggleIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(this, PlaybackCommandReceiver::class.java).setAction(ACTION_TOGGLE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openIntent = PendingIntent.getActivity(
            this,
            2,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.playback_notification_title))
            .setContentText(getString(R.string.playback_notification_text))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, getString(R.string.pause), toggleIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.playback_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "blanket_playback"
        const val ACTION_TOGGLE = "com.vinberg88.blanketforandroid.TOGGLE_PLAYBACK"
        const val ACTION_START = "com.vinberg88.blanketforandroid.START_PLAYBACK"
        private const val NOTIFICATION_ID = 120
    }
}
