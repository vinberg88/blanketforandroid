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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PlaybackController {
    private val initMutex = Mutex()

    private var appContext: Context? = null
    private var prefsRepository: PreferencesRepository? = null
    private var audioPlayer: AudioPlayer? = null

    suspend fun initialize(context: Context) {
        withInitializedController(context) { _, _, _ -> }
    }

    suspend fun startPlayback(context: Context, soundStates: Map<String, SoundState>? = null) {
        withInitializedController(context) { applicationContext, repository, player ->
            startPlaybackLocked(applicationContext, repository, player, soundStates)
        }
    }

    suspend fun togglePlayback(
        context: Context,
        soundStates: Map<String, SoundState>? = null
    ): Boolean {
        return withInitializedController(context) { applicationContext, repository, player ->
            val newPlaying = !repository.isPlaying.first()
            repository.setIsPlaying(newPlaying)

            if (newPlaying) {
                startPlaybackLocked(applicationContext, repository, player, soundStates)
            } else {
                player.pauseAll()
                applicationContext.stopService(Intent(applicationContext, PlaybackForegroundService::class.java))
            }

            newPlaying
        }
    }

    suspend fun syncSoundState(
        context: Context,
        soundId: String,
        isEnabled: Boolean,
        volume: Float,
        isPlaying: Boolean
    ) {
        withInitializedController(context) { _, repository, player ->
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
    }

    suspend fun updateSoundVolume(context: Context, soundId: String, volume: Float) {
        withInitializedController(context) { _, repository, player ->
            ensureCustomSoundsLoaded(repository, player)
            player.setVolume(soundId, volume)
        }
    }

    suspend fun loadCustomSound(context: Context, sound: Sound, uri: Uri): Boolean {
        return withInitializedController(context) { _, _, player ->
            player.loadSoundFromUri(sound, uri)
            player.hasSound(sound.id)
        }
    }

    suspend fun releaseSound(context: Context, soundId: String) {
        withInitializedController(context) { _, _, player ->
            player.releaseSound(soundId)
        }
    }

    suspend fun pauseAll(context: Context) {
        withInitializedController(context) { _, _, player ->
            player.pauseAll()
        }
    }

    suspend fun setMasterVolume(context: Context, volume: Float) {
        withInitializedController(context) { _, _, player ->
            player.setMasterVolume(volume)
        }
    }

    fun releaseIfNotPlaying(isPlaying: Boolean) {
        if (!isPlaying) {
            runBlocking {
                initMutex.withLock {
                    audioPlayer?.release()
                    audioPlayer = null
                    prefsRepository = null
                    appContext = null
                }
            }
        }
    }

    suspend fun stopForegroundService(context: Context) {
        withInitializedController(context) { applicationContext, _, _ ->
            applicationContext.stopService(
                Intent(applicationContext, PlaybackForegroundService::class.java)
            )
        }
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

    private suspend fun startPlaybackLocked(
        applicationContext: Context,
        repository: PreferencesRepository,
        player: AudioPlayer,
        soundStates: Map<String, SoundState>? = null
    ) {
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
            applicationContext,
            Intent(applicationContext, PlaybackForegroundService::class.java)
        )
    }

    private suspend fun <T> withInitializedController(
        context: Context,
        block: suspend (Context, PreferencesRepository, AudioPlayer) -> T
    ): T = initMutex.withLock {
        if (audioPlayer == null || prefsRepository == null || appContext == null) {
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

        block(
            requireNotNull(appContext),
            requireNotNull(prefsRepository),
            requireNotNull(audioPlayer)
        )
    }
}
