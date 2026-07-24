package com.vinberg88.blanketforandroid.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vinberg88.blanketforandroid.MainActivity
import com.vinberg88.blanketforandroid.R
import com.vinberg88.blanketforandroid.audio.AudioPlayer
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.availableSounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaybackForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val externalAudioPlayer by lazy { AudioPlayer(applicationContext) }
    private val externalLoadedSoundIds = mutableSetOf<String>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE -> {
                val toggle = PlaybackController.onTogglePlayback
                if (toggle != null) {
                    toggle.invoke()
                } else {
                    serviceScope.launch { toggleFromPersistedState() }
                }
            }
            ACTION_SYNC_FROM_PREFERENCES -> {
                serviceScope.launch { startExternalPlaybackFromPersistedState() }
            }
            ACTION_RELEASE_EXTERNAL_PLAYBACK -> {
                stopExternalPlayback()
                stopSelf()
                return START_NOT_STICKY
            }
        }
        createChannel()
        val toggleIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(this, PlaybackCommandReceiver::class.java).setAction(ACTION_TOGGLE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openIntent = PendingIntent.getActivity(this, 2, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.playback_notification_title))
            .setContentText(getString(R.string.playback_notification_text))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, getString(R.string.pause), toggleIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

    private suspend fun toggleFromPersistedState() {
        val repository = PreferencesRepository(this)
        val currentlyPlaying = repository.isPlaying.first()
        repository.setIsPlaying(!currentlyPlaying)
        if (currentlyPlaying) {
            stopExternalPlayback()
            stopSelf()
        } else {
            startExternalPlaybackFromPersistedState()
        }
    }

    private suspend fun startExternalPlaybackFromPersistedState() {
        val repository = PreferencesRepository(this)
        val sounds = availableSounds + repository.customSounds.first().map {
            Sound(
                id = it.id,
                fileName = it.uriString,
                displayName = it.displayName,
                icon = com.vinberg88.blanketforandroid.model.iconForName(it.iconName),
                iconName = it.iconName,
                isCustom = true
            )
        }

        ensureExternalSoundsLoaded(sounds)
        val enabledSounds = mutableSetOf<String>()
        sounds.forEach { sound ->
            val state = repository.getSoundState(sound.id).first()
            externalAudioPlayer.setVolume(sound.id, state.volume)
            if (state.isEnabled) enabledSounds.add(sound.id)
        }
        externalAudioPlayer.setMasterVolume(repository.masterVolume.first())
        externalAudioPlayer.resumeAll(enabledSounds)
    }

    private suspend fun ensureExternalSoundsLoaded(sounds: List<Sound>) {
        if (externalLoadedSoundIds.containsAll(sounds.map { it.id })) return
        sounds.forEach { sound ->
            if (externalLoadedSoundIds.add(sound.id)) {
                if (sound.isCustom) {
                    externalAudioPlayer.loadSoundFromUri(sound, android.net.Uri.parse(sound.fileName))
                } else {
                    externalAudioPlayer.loadSound(sound)
                }
            }
        }
    }

    private fun stopExternalPlayback() {
        externalAudioPlayer.pauseAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "blanket_playback"
        const val ACTION_TOGGLE = "com.vinberg88.blanketforandroid.TOGGLE_PLAYBACK"
        const val ACTION_SYNC_FROM_PREFERENCES = "com.vinberg88.blanketforandroid.SYNC_PLAYBACK_FROM_PREFERENCES"
        const val ACTION_RELEASE_EXTERNAL_PLAYBACK = "com.vinberg88.blanketforandroid.RELEASE_EXTERNAL_PLAYBACK"
        private const val NOTIFICATION_ID = 120
    }
}
