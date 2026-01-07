package com.vinberg88.blanketforandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vinberg88.blanketforandroid.audio.AudioPlayer
import com.vinberg88.blanketforandroid.data.PreferencesRepository
import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BlanketViewModel(application: Application) : AndroidViewModel(application) {
    private val audioPlayer = AudioPlayer(application)
    private val prefsRepository = PreferencesRepository(application)

    private val _soundStates = MutableStateFlow<Map<String, SoundState>>(emptyMap())
    val soundStates: StateFlow<Map<String, SoundState>> = _soundStates.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private var hasAutoStarted = false

    init {
        // Load all sounds
        viewModelScope.launch {
            availableSounds.forEach { sound ->
                audioPlayer.loadSound(sound)
            }
        }

        // Load saved playing state
        viewModelScope.launch {
            prefsRepository.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }

        // Load saved sound states
        viewModelScope.launch {
            combine(
                availableSounds.map { sound ->
                    prefsRepository.getSoundState(sound.id)
                }
            ) { states ->
                states.associateBy { it.soundId }
            }.collect { states ->
                _soundStates.value = states
                
                // Auto-restore playback on first load if was playing
                if (!hasAutoStarted && _isPlaying.value) {
                    hasAutoStarted = true
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
            } else {
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

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
