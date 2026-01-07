package com.vinberg88.blanketforandroid.model

data class SoundState(
    val soundId: String,
    val isEnabled: Boolean = false,
    val volume: Float = 0.5f
)

data class AppState(
    val isPlaying: Boolean = false,
    val soundStates: Map<String, SoundState> = emptyMap()
)
