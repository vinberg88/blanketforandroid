package com.vinberg88.blanketforandroid.playback

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.vinberg88.blanketforandroid.audio.AudioPlayer
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import com.vinberg88.blanketforandroid.model.iconForName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PlaybackController {
    private val initMutex = Mutex()

    private var appContext: Context? = null
    private var prefsRepository: PreferencesRepository? = null
    private var audioPlayer: AudioPlayer? = null

    suspend fun initialize(context: Context) {
        if (audioPlayer != null) return

        initMutex.withLock {
            if (audioPlayer != null) return

            val applicationContext = context.applicationContext
            val player = AudioPlayer(applicationContext)
            availableSounds.forEach { sound ->
                player.loadSound(sound)
            }

            val repository = PreferencesRepository(applicationContext)
            player.setMasterVolume(repository.masterVolume.first())

            appContext = applicationContext
            prefsRepository = repository
            audioPlayer = player
        }
    }

    suspend fun startPlayback(context: Context, soundStates: Map<String, SoundState>? = null) {
        initialize(context)

        val repository = requireNotNull(prefsRepository)
        val player = requireNotNull(audioPlayer)
        player.setMasterVolume(repository.masterVolume.first())
        ensureCustomSoundsLoaded(repository, player)

        val states = soundStates ?: loadPersistedSoundStates(repository)
        states.values.forEach { state ->
            player.setVolume(state.soundId, state.volume)
            if (state.isEnabled) {
                player.play(state.soundId)
            } else {
                player.pause(state.soundId)
            }
        }

        ContextCompat.startForegroundService(
            requireNotNull(appContext),
            Intent(requireNotNull(appContext), PlaybackForegroundService::class.java)
        )
    }

    suspend fun togglePlayback(
        context: Context,
        soundStates: Map<String, SoundState>? = null
    ): Boolean {
        initialize(context)

        val repository = requireNotNull(prefsRepository)
        val newPlaying = !repository.isPlaying.first()
        repository.setIsPlaying(newPlaying)

        if (newPlaying) {
            startPlayback(context, soundStates)
        } else {
            audioPlayer?.pauseAll()
            stopForegroundService()
        }

        return newPlaying
    }

    suspend fun syncSoundState(
        context: Context,
        soundId: String,
        isEnabled: Boolean,
        volume: Float,
        isPlaying: Boolean
    ) {
        initialize(context)

        val repository = requireNotNull(prefsRepository)
        val player = requireNotNull(audioPlayer)
        ensureCustomSoundsLoaded(repository, player)

        player.setVolume(soundId, volume)
        if (isPlaying) {
            if (isEnabled) {
                player.play(soundId)
            } else {
                player.pause(soundId)
            }
        }
    }

    suspend fun updateSoundVolume(context: Context, soundId: String, volume: Float) {
        initialize(context)

        val repository = requireNotNull(prefsRepository)
        val player = requireNotNull(audioPlayer)
        ensureCustomSoundsLoaded(repository, player)
        player.setVolume(soundId, volume)
    }

    suspend fun loadCustomSound(context: Context, sound: Sound, uri: Uri): Boolean {
        initialize(context)

        val player = requireNotNull(audioPlayer)
        player.loadSoundFromUri(sound, uri)
        return player.hasSound(sound.id)
    }

    fun releaseSound(soundId: String) {
        audioPlayer?.releaseSound(soundId)
    }

    fun pauseAll() {
        audioPlayer?.pauseAll()
    }

    fun setMasterVolume(volume: Float) {
        audioPlayer?.setMasterVolume(volume)
    }

    fun releaseIfNotPlaying(isPlaying: Boolean) {
        if (!isPlaying) {
            audioPlayer?.release()
            audioPlayer = null
            prefsRepository = null
            appContext = null
        }
    }

    fun stopForegroundService() {
        val context = appContext ?: return
        context.stopService(Intent(context, PlaybackForegroundService::class.java))
    }

    private suspend fun ensureCustomSoundsLoaded(
        repository: PreferencesRepository,
        player: AudioPlayer
    ) {
        repository.customSounds.first().forEach { metadata ->
            if (!player.hasSound(metadata.id)) {
                player.loadSoundFromUri(
                    sound = Sound(
                        id = metadata.id,
                        fileName = metadata.uriString,
                        displayName = metadata.displayName,
                        icon = iconForName(metadata.iconName),
                        iconName = metadata.iconName,
                        isCustom = true
                    ),
                    uri = Uri.parse(metadata.uriString)
                )
            }
        }
    }

    private suspend fun loadPersistedSoundStates(
        repository: PreferencesRepository
    ): Map<String, SoundState> {
        val soundIds = availableSounds.map { it.id } + repository.customSounds.first().map { it.id }
        return soundIds.associateWith { soundId ->
            repository.getSoundState(soundId).first()
        }
    }
}
