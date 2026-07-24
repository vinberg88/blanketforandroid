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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

object PlaybackController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val initializationMutex = Mutex()
    private val soundLoadMutex = Mutex()
    private val toggleMutex = Mutex()
    private val loadedSoundIds = ConcurrentHashMap.newKeySet<String>()

    private lateinit var prefsRepository: PreferencesRepository
    private lateinit var audioPlayer: AudioPlayer
    private var initializationJob: kotlinx.coroutines.Deferred<Unit>? = null
    private var masterVolumeObserverJob: Job? = null

    suspend fun ensureInitialized(context: Context) {
        val appContext = context.applicationContext
        val job = initializationMutex.withLock {
            if (!::audioPlayer.isInitialized) {
                prefsRepository = PreferencesRepository(appContext)
                audioPlayer = AudioPlayer(appContext)
            }

            if (masterVolumeObserverJob == null) {
                masterVolumeObserverJob = scope.launch {
                    prefsRepository.masterVolume.collect { volume ->
                        audioPlayer.setMasterVolume(volume)
                    }
                }
            }

            if (initializationJob == null) {
                initializationJob = scope.async {
                    availableSounds.forEach { sound ->
                        loadBuiltInSound(sound)
                    }
                    prefsRepository.customSounds.first().forEach { metadata ->
                        loadCustomSound(metadata)
                    }
                }
            }

            initializationJob!!
        }

        job.await()
    }

    suspend fun resumePlayback(context: Context, states: Collection<SoundState>) {
        ensureInitialized(context)
        val enabledSoundIds = states.filter { it.isEnabled }.map { state ->
            audioPlayer.setVolume(state.soundId, state.volume)
            state.soundId
        }.toSet()

        audioPlayer.resumeAll(enabledSoundIds)
        PlaybackForegroundService.start(context)
    }

    suspend fun togglePlayback(context: Context) {
        ensureInitialized(context)
        toggleMutex.withLock {
            if (prefsRepository.isPlaying.first()) {
                prefsRepository.setIsPlaying(false)
                pauseAll(context)
            } else {
                prefsRepository.setIsPlaying(true)
                resumePlayback(context, currentSoundStates())
            }
        }
    }

    suspend fun play(context: Context, soundId: String, volume: Float) {
        ensureInitialized(context)
        audioPlayer.setVolume(soundId, volume)
        audioPlayer.play(soundId)
    }

    suspend fun pause(context: Context, soundId: String) {
        ensureInitialized(context)
        audioPlayer.pause(soundId)
    }

    suspend fun setVolume(context: Context, soundId: String, volume: Float) {
        ensureInitialized(context)
        audioPlayer.setVolume(soundId, volume)
    }

    suspend fun setMasterVolume(context: Context, volume: Float) {
        ensureInitialized(context)
        audioPlayer.setMasterVolume(volume)
    }

    suspend fun pauseAll(context: Context) {
        ensureInitialized(context)
        audioPlayer.pauseAll()
        PlaybackForegroundService.stop(context)
    }

    suspend fun loadCustomSound(context: Context, sound: Sound, uri: Uri): Boolean {
        ensureInitialized(context)
        return soundLoadMutex.withLock {
            if (sound.id in loadedSoundIds) return@withLock true

            if (loadedSoundIds.add(sound.id)) {
                val loaded = audioPlayer.loadSoundFromUri(sound, uri)
                if (!loaded) {
                    loadedSoundIds.remove(sound.id)
                }
                loaded
            } else {
                true
            }
        }
    }

    suspend fun releaseSound(context: Context, soundId: String) {
        ensureInitialized(context)
        soundLoadMutex.withLock {
            audioPlayer.releaseSound(soundId)
            loadedSoundIds.remove(soundId)
        }
    }

    private suspend fun currentSoundStates(): List<SoundState> {
        val customIds = prefsRepository.customSounds.first().map { it.id }
        return (availableSounds.map { it.id } + customIds)
            .distinct()
            .map { soundId -> prefsRepository.getSoundState(soundId).first() }
    }

    private suspend fun loadBuiltInSound(sound: Sound) {
        soundLoadMutex.withLock {
            if (loadedSoundIds.add(sound.id)) {
                val loaded = audioPlayer.loadSound(sound)
                if (!loaded) {
                    loadedSoundIds.remove(sound.id)
                }
            }
        }
    }

    private suspend fun loadCustomSound(metadata: CustomSoundMetadata) {
        soundLoadMutex.withLock {
            if (loadedSoundIds.add(metadata.id)) {
                val sound = Sound(
                    id = metadata.id,
                    fileName = metadata.uriString,
                    displayName = metadata.displayName,
                    icon = iconForName(metadata.iconName),
                    iconName = metadata.iconName,
                    isCustom = true
                )
                val loaded = audioPlayer.loadSoundFromUri(sound, Uri.parse(metadata.uriString))
                if (!loaded) {
                    loadedSoundIds.remove(metadata.id)
                }
            }
        }
    }
}
