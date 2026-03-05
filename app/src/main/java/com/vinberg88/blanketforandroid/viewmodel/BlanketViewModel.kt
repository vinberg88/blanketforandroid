package com.vinberg88.blanketforandroid.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vinberg88.blanketforandroid.audio.AudioPlayer
import com.vinberg88.blanketforandroid.data.CustomSoundMetadata
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BlanketViewModel(application: Application) : AndroidViewModel(application) {
    private val audioPlayer = AudioPlayer(application)
    private val prefsRepository = PreferencesRepository(application)

    private val _soundStates = MutableStateFlow<Map<String, SoundState>>(emptyMap())
    val soundStates: StateFlow<Map<String, SoundState>> = _soundStates.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _customSounds = MutableStateFlow<List<Sound>>(emptyList())
    val customSounds: StateFlow<List<Sound>> = _customSounds.asStateFlow()

    val allSounds: StateFlow<List<Sound>> = _customSounds
        .map { custom -> availableSounds + custom }
        .stateIn(viewModelScope, SharingStarted.Eagerly, availableSounds)

    private val hasAutoStarted = AtomicBoolean(false)

    init {
        // Load saved playing state
        viewModelScope.launch {
            prefsRepository.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }

        // Load sounds and observe state changes
        viewModelScope.launch {
            // Load built-in sounds
            availableSounds.forEach { sound ->
                audioPlayer.loadSound(sound)
            }

            // Load persisted custom sounds
            val savedCustomSounds = prefsRepository.customSounds.first()
            val loadedCustomSounds = savedCustomSounds.mapNotNull { metadata ->
                try {
                    val sound = Sound(
                        id = metadata.id,
                        fileName = metadata.uriString,
                        displayName = metadata.displayName,
                        icon = Icons.Default.MusicNote,
                        isCustom = true
                    )
                    audioPlayer.loadSoundFromUri(sound, Uri.parse(metadata.uriString))
                    sound
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
                    states.values.forEach { state ->
                        if (state.isEnabled) {
                            audioPlayer.setVolume(state.soundId, state.volume)
                            audioPlayer.play(state.soundId)
                        }
                    }
                }
            }
        }
    }

    fun toggleSound(soundId: String) {
        viewModelScope.launch {
            val currentState = _soundStates.value[soundId]
            val newEnabled = !(currentState?.isEnabled ?: false)

            prefsRepository.setSoundEnabled(soundId, newEnabled)

            if (newEnabled && _isPlaying.value) {
                audioPlayer.play(soundId)
            } else if (!newEnabled && _isPlaying.value) {
                audioPlayer.pause(soundId)
            }
        }
    }

    fun setSoundVolume(soundId: String, volume: Float) {
        viewModelScope.launch {
            prefsRepository.setSoundVolume(soundId, volume)
            audioPlayer.setVolume(soundId, volume)
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            val newPlaying = !_isPlaying.value
            prefsRepository.setIsPlaying(newPlaying)

            if (newPlaying) {
                val enabledSounds = _soundStates.value
                    .filter { it.value.isEnabled }
                    .keys
                audioPlayer.resumeAll(enabledSounds)
            } else {
                audioPlayer.pauseAll()
            }
        }
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
                isCustom = true
            )
            audioPlayer.loadSoundFromUri(sound, uri)
            prefsRepository.saveCustomSound(
                CustomSoundMetadata(
                    id = soundId,
                    displayName = displayName,
                    uriString = uri.toString()
                )
            )
            _customSounds.value = _customSounds.value + sound
        }
    }

    fun removeCustomSound(soundId: String) {
        viewModelScope.launch {
            audioPlayer.pause(soundId)
            audioPlayer.releaseSound(soundId)
            prefsRepository.removeCustomSound(soundId)
            _customSounds.value = _customSounds.value.filter { it.id != soundId }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
