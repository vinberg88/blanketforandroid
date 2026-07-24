package com.vinberg88.blanketforandroid.playback

import android.content.Context
import android.net.Uri
import com.vinberg88.blanketforandroid.audio.AudioPlayer
import com.vinberg88.blanketforandroid.data.CustomSoundMetadata
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import com.vinberg88.blanketforandroid.model.iconForName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

object PlaybackController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val initializationMutex = Mutex()
    private val loadedSoundIds = ConcurrentHashMap.newKeySet<String>()

    private var prefsRepository: PreferencesRepository? = null
    private var audioPlayer: AudioPlayer? = null
    private var initializationJob: kotlinx.coroutines.Deferred<Unit>? = null
    private var masterVolumeObserverStarted = false

    suspend fun ensureInitialized(context: Context) {
        val appContext = context.applicationContext
        val job = initializationMutex.withLock {
            if (audioPlayer == null) {
                prefsRepository = PreferencesRepository(appContext)
                audioPlayer = AudioPlayer(appContext)
            }

            if (!masterVolumeObserverStarted) {
                masterVolumeObserverStarted = true
                val repository = requireNotNull(prefsRepository)
                val player = requireNotNull(audioPlayer)
                scope.launch {
                    repository.masterVolume.collect { volume ->
                        player.setMasterVolume(volume)
                    }
                }
            }

            initializationJob ?: scope.async {
                availableSounds.forEach { sound ->
                    loadBuiltInSound(sound)
                }
                requireNotNull(prefsRepository).customSounds.first().forEach { metadata ->
                    loadCustomSound(metadata)
                }
            }.also { initializationJob = it }
        }

        job.await()
    }

    suspend fun resumePlayback(context: Context, states: Collection<SoundState>) {
        ensureInitialized(context)
        val player = requireNotNull(audioPlayer)
        val enabledSoundIds = states.filter { it.isEnabled }.map { state ->
            player.setVolume(state.soundId, state.volume)
            state.soundId
        }.toSet()

        player.resumeAll(enabledSoundIds)
        PlaybackForegroundService.start(context)
    }

    suspend fun togglePlayback(context: Context) {
        ensureInitialized(context)
        val repository = requireNotNull(prefsRepository)

        if (repository.isPlaying.first()) {
            repository.setIsPlaying(false)
            pauseAll(context)
        } else {
            repository.setIsPlaying(true)
            resumePlayback(context, currentSoundStates(repository))
        }
    }

    suspend fun play(context: Context, soundId: String, volume: Float) {
        ensureInitialized(context)
        val player = requireNotNull(audioPlayer)
        player.setVolume(soundId, volume)
        player.play(soundId)
    }

    suspend fun pause(context: Context, soundId: String) {
        ensureInitialized(context)
        requireNotNull(audioPlayer).pause(soundId)
    }

    suspend fun setVolume(context: Context, soundId: String, volume: Float) {
        ensureInitialized(context)
        requireNotNull(audioPlayer).setVolume(soundId, volume)
    }

    suspend fun setMasterVolume(context: Context, volume: Float) {
        ensureInitialized(context)
        requireNotNull(audioPlayer).setMasterVolume(volume)
    }

    suspend fun pauseAll(context: Context) {
        ensureInitialized(context)
        requireNotNull(audioPlayer).pauseAll()
        PlaybackForegroundService.stop(context)
    }

    suspend fun loadCustomSound(context: Context, sound: Sound, uri: Uri) {
        ensureInitialized(context)
        if (loadedSoundIds.add(sound.id)) {
            requireNotNull(audioPlayer).loadSoundFromUri(sound, uri)
        }
    }

    suspend fun releaseSound(context: Context, soundId: String) {
        ensureInitialized(context)
        requireNotNull(audioPlayer).releaseSound(soundId)
        loadedSoundIds.remove(soundId)
    }

    private suspend fun currentSoundStates(repository: PreferencesRepository): List<SoundState> {
        val customIds = repository.customSounds.first().map { it.id }
        return (availableSounds.map { it.id } + customIds)
            .distinct()
            .map { soundId -> repository.getSoundState(soundId).first() }
    }

    private suspend fun loadBuiltInSound(sound: Sound) {
        if (loadedSoundIds.add(sound.id)) {
            requireNotNull(audioPlayer).loadSound(sound)
        }
    }

    private suspend fun loadCustomSound(metadata: CustomSoundMetadata) {
        if (loadedSoundIds.add(metadata.id)) {
            val sound = Sound(
                id = metadata.id,
                fileName = metadata.uriString,
                displayName = metadata.displayName,
                icon = iconForName(metadata.iconName),
                iconName = metadata.iconName,
                isCustom = true
            )
            requireNotNull(audioPlayer).loadSoundFromUri(sound, Uri.parse(metadata.uriString))
        }
    }
}
