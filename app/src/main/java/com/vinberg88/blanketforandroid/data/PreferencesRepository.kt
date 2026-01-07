package com.vinberg88.blanketforandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vinberg88.blanketforandroid.model.SoundState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "blanket_settings")

class PreferencesRepository(private val context: Context) {
    
    private val IS_PLAYING_KEY = booleanPreferencesKey("is_playing")
    
    private fun soundEnabledKey(soundId: String) = booleanPreferencesKey("sound_enabled_$soundId")
    private fun soundVolumeKey(soundId: String) = floatPreferencesKey("sound_volume_$soundId")

    val isPlaying: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PLAYING_KEY] ?: false
    }

    fun getSoundState(soundId: String): Flow<SoundState> = context.dataStore.data.map { preferences ->
        SoundState(
            soundId = soundId,
            isEnabled = preferences[soundEnabledKey(soundId)] ?: false,
            volume = preferences[soundVolumeKey(soundId)] ?: 0.5f
        )
    }

    suspend fun setIsPlaying(isPlaying: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PLAYING_KEY] = isPlaying
        }
    }

    suspend fun setSoundEnabled(soundId: String, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[soundEnabledKey(soundId)] = enabled
        }
    }

    suspend fun setSoundVolume(soundId: String, volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[soundVolumeKey(soundId)] = volume
        }
    }
}
