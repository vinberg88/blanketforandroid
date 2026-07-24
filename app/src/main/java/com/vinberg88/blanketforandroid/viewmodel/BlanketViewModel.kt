package com.vinberg88.blanketforandroid.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vinberg88.blanketforandroid.data.CustomSoundMetadata
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.data.SavedMix
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import com.vinberg88.blanketforandroid.model.iconForName
import com.vinberg88.blanketforandroid.playback.PlaybackController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class)
class BlanketViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsRepository = PreferencesRepository(application)

    private val _soundStates = MutableStateFlow<Map<String, SoundState>>(emptyMap())
    val soundStates: StateFlow<Map<String, SoundState>> = _soundStates.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _masterVolume = MutableStateFlow(1f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()
    private var lastAudibleMasterVolume = 1f

    private val _selectedPreset = MutableStateFlow("Default")
    val selectedPreset: StateFlow<String> = _selectedPreset.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow<Int?>(null)
    val sleepTimerMinutes: StateFlow<Int?> = _sleepTimerMinutes.asStateFlow()

    private val _sleepTimerEndsAt = MutableStateFlow<Long?>(null)
    val sleepTimerEndsAt: StateFlow<Long?> = _sleepTimerEndsAt.asStateFlow()

    val favoriteSoundIds: StateFlow<Set<String>> = prefsRepository.favoriteSoundIds
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    val savedMixes: StateFlow<List<SavedMix>> = prefsRepository.savedMixes
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _customSounds = MutableStateFlow<List<Sound>>(emptyList())
    val customSounds: StateFlow<List<Sound>> = _customSounds.asStateFlow()

    val allSounds: StateFlow<List<Sound>> = _customSounds
        .map { custom -> availableSounds + custom }
        .stateIn(viewModelScope, SharingStarted.Eagerly, availableSounds)

    private val hasAutoStarted = AtomicBoolean(false)
    private var sleepTimerJob: Job? = null

    init {
        // Load saved playing state
        viewModelScope.launch {
            prefsRepository.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }

        viewModelScope.launch {
            prefsRepository.masterVolume.collect { volume ->
                _masterVolume.value = volume
                if (volume > 0.01f) {
                    lastAudibleMasterVolume = volume
                }
                PlaybackController.setMasterVolume(getApplication(), volume)
            }
        }

        // Load sounds and observe state changes
        viewModelScope.launch {
            // Load persisted custom sounds
            val savedCustomSounds = prefsRepository.customSounds.first()
            val loadedCustomSounds = savedCustomSounds.mapNotNull { metadata ->
                try {
                    val sound = Sound(
                        id = metadata.id,
                        fileName = metadata.uriString,
                        displayName = metadata.displayName,
                        icon = iconForName(metadata.iconName),
                        iconName = metadata.iconName,
                        isCustom = true
                    )
                    if (PlaybackController.loadCustomSound(getApplication(), sound, Uri.parse(metadata.uriString))) {
                        sound
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            _customSounds.value = loadedCustomSounds

            // Observe combined sound states dynamically
            _customSounds.flatMapLatest { customList ->
                val allSoundsList = availableSounds + customList
                combine(allSoundsList.map { sound -> prefsRepository.getSoundState(sound.id) }) { states ->
                    states.associateBy { it.soundId }
                }
            }.collect { states ->
                _soundStates.value = states

                // Auto-restore playback on first load if was playing
                if (hasAutoStarted.compareAndSet(false, true) && _isPlaying.value) {
                    PlaybackController.startPlayback(getApplication(), states)
                }
            }
        }
    }

    fun toggleSound(soundId: String) {
        viewModelScope.launch {
            val currentState = _soundStates.value[soundId]
            val newEnabled = !(currentState?.isEnabled ?: false)
            val volume = currentState?.volume ?: DEFAULT_SOUND_VOLUME

            prefsRepository.setSoundEnabled(soundId, newEnabled)
            PlaybackController.syncSoundState(getApplication(), soundId, newEnabled, volume, _isPlaying.value)
        }
    }

    fun setSoundVolume(soundId: String, volume: Float) {
        viewModelScope.launch {
            prefsRepository.setSoundVolume(soundId, volume)
            PlaybackController.updateSoundVolume(getApplication(), soundId, volume)
        }
    }

    fun setMasterVolume(volume: Float) {
        viewModelScope.launch {
            if (volume > 0.01f) {
                lastAudibleMasterVolume = volume
            }
            prefsRepository.setMasterVolume(volume)
        }
    }

    fun toggleMasterMute() {
        setMasterVolume(
            if (_masterVolume.value > 0.01f) 0f
            else lastAudibleMasterVolume.coerceAtLeast(0.5f)
        )
    }

    fun applyPreset(name: String) {
        val mix = when (name) {
            "Nature" -> mapOf("rain" to 0.55f, "stream" to 0.45f, "birds" to 0.35f)
            "Focus" -> mapOf("rain" to 0.35f, "coffee_shop" to 0.28f, "pink_noise" to 0.22f)
            "Sleep" -> mapOf("waves" to 0.50f, "summer_night" to 0.30f, "white_noise" to 0.18f)
            else -> mapOf("rain" to 0.32f, "storm" to 0.52f, "waves" to 0.55f)
        }

        _selectedPreset.value = name
        viewModelScope.launch {
            availableSounds.forEach { sound ->
                val volume = mix[sound.id] ?: DEFAULT_SOUND_VOLUME
                val enabled = sound.id in mix
                prefsRepository.setSoundVolume(sound.id, volume)
                prefsRepository.setSoundEnabled(sound.id, enabled)
                PlaybackController.syncSoundState(
                    getApplication(),
                    sound.id,
                    enabled,
                    volume,
                    _isPlaying.value
                )
            }
        }
    }

    fun applySavedMix(mix: SavedMix) {
        _selectedPreset.value = mix.name
        viewModelScope.launch {
            allSounds.value.forEach { sound ->
                val enabled = sound.id in mix.soundIds
                val volume = mix.volumes[sound.id] ?: DEFAULT_SOUND_VOLUME
                prefsRepository.setSoundEnabled(sound.id, enabled)
                prefsRepository.setSoundVolume(sound.id, volume)
                PlaybackController.syncSoundState(
                    getApplication(),
                    sound.id,
                    enabled,
                    volume,
                    _isPlaying.value
                )
            }
        }
    }

    fun saveCurrentMix(name: String) {
        val cleanName = name.trim().take(40)
        if (cleanName.isEmpty()) return
        val states = _soundStates.value
        val enabled = states.values.filter { it.isEnabled }.map { it.soundId }.toSet()
        val volumes = savedMixVolumesForEnabledSounds(states)
        viewModelScope.launch {
            prefsRepository.saveMix(SavedMix("mix_${java.util.UUID.randomUUID()}", cleanName, enabled, volumes))
            _selectedPreset.value = cleanName
        }
    }

    fun removeSavedMix(mix: SavedMix) {
        viewModelScope.launch { prefsRepository.removeMix(mix) }
    }

    fun toggleFavorite(soundId: String) {
        viewModelScope.launch { prefsRepository.toggleFavorite(soundId) }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            val newPlaying = PlaybackController.togglePlayback(getApplication(), _soundStates.value)
            if (!newPlaying) {
                cancelSleepTimer()
            }
        }
    }

    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        _sleepTimerMinutes.value = minutes
        _sleepTimerEndsAt.value = System.currentTimeMillis() + minutes * 60_000L
        sleepTimerJob = viewModelScope.launch {
            val fadeDurationMs = 30_000L
            if (minutes * 60_000L > fadeDurationMs) delay(minutes * 60_000L - fadeDurationMs)
            fadeOutAndStop(fadeDurationMs)
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerMinutes.value = null
        _sleepTimerEndsAt.value = null
    }

    private suspend fun fadeOutAndStop(durationMs: Long) {
        val originalMasterVolume = _masterVolume.value
        val steps = 15
        var completedFade = false
        try {
            repeat(steps) { step ->
                val progress = (step + 1).toFloat() / steps
                PlaybackController.setMasterVolume(
                    getApplication(),
                    originalMasterVolume * (1f - progress)
                )
                delay(durationMs / steps)
            }
            completedFade = true
        } finally {
            if (!completedFade) {
                PlaybackController.setMasterVolume(getApplication(), originalMasterVolume)
            }
        }
        stopAllSounds()
    }

    private suspend fun stopAllSounds() {
        _soundStates.value.values.forEach { state ->
            if (state.isEnabled) {
                prefsRepository.setSoundEnabled(state.soundId, false)
            }
        }
        prefsRepository.setIsPlaying(false)
        PlaybackController.pauseAll(getApplication())
        PlaybackController.stopForegroundService(getApplication())
        sleepTimerJob = null
        _sleepTimerMinutes.value = null
        _sleepTimerEndsAt.value = null
    }

    fun addCustomSound(uri: Uri, displayName: String) {
        viewModelScope.launch {
            // Take persistent permission so the URI remains accessible after restart
            try {
                getApplication<Application>().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Permission may not be persistable for all URI types; continue anyway
            }

            val soundId = "custom_${java.util.UUID.randomUUID()}"
            val sound = Sound(
                id = soundId,
                fileName = uri.toString(),
                displayName = displayName,
                icon = Icons.Default.MusicNote,
                iconName = "music_note",
                isCustom = true
            )
            if (!PlaybackController.loadCustomSound(getApplication(), sound, uri)) {
                return@launch
            }
            prefsRepository.saveCustomSound(
                CustomSoundMetadata(
                    id = soundId,
                    displayName = displayName,
                    uriString = uri.toString(),
                    iconName = "music_note"
                )
            )
            _customSounds.value = _customSounds.value + sound
        }
    }

    fun updateCustomSound(soundId: String, displayName: String, iconName: String) {
        viewModelScope.launch {
            prefsRepository.updateCustomSound(soundId, displayName, iconName)
            _customSounds.value = _customSounds.value.map { sound ->
                if (sound.id == soundId) {
                    sound.copy(
                        displayName = displayName,
                        icon = iconForName(iconName),
                        iconName = iconName
                    )
                } else {
                    sound
                }
            }
        }
    }

    fun removeCustomSound(soundId: String) {
        viewModelScope.launch {
            PlaybackController.releaseSound(getApplication(), soundId)
            prefsRepository.removeCustomSound(soundId)
            _customSounds.value = _customSounds.value.filter { it.id != soundId }
        }
    }

    override fun onCleared() {
        super.onCleared()
        PlaybackController.releaseIfNotPlaying(_isPlaying.value)
    }
}

internal fun savedMixVolumesForEnabledSounds(states: Map<String, SoundState>): Map<String, Float> =
    states.values
        .filter { it.isEnabled }
        .associate { it.soundId to it.volume }

private const val DEFAULT_SOUND_VOLUME = 0.45f
