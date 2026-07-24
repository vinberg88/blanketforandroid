package com.vinberg88.blanketforandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vinberg88.blanketforandroid.model.SoundState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "blanket_settings")

data class CustomSoundMetadata(
    val id: String,
    val displayName: String,
    val uriString: String,
    val iconName: String = "music_note"
)

class PreferencesRepository(private val context: Context) {
    
    private val IS_PLAYING_KEY = booleanPreferencesKey("is_playing")
    private val MASTER_VOLUME_KEY = floatPreferencesKey("master_volume")
    private val CUSTOM_SOUND_IDS_KEY = stringSetPreferencesKey("custom_sound_ids")

    private fun soundEnabledKey(soundId: String) = booleanPreferencesKey("sound_enabled_$soundId")
    private fun soundVolumeKey(soundId: String) = floatPreferencesKey("sound_volume_$soundId")
    private fun customSoundNameKey(soundId: String) = stringPreferencesKey("custom_sound_name_$soundId")
    private fun customSoundUriKey(soundId: String) = stringPreferencesKey("custom_sound_uri_$soundId")
    private fun customSoundIconKey(soundId: String) = stringPreferencesKey("custom_sound_icon_$soundId")

    val isPlaying: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PLAYING_KEY] ?: false
    }

    val customSounds: Flow<List<CustomSoundMetadata>> = context.dataStore.data.map { preferences ->
        val ids = preferences[CUSTOM_SOUND_IDS_KEY] ?: emptySet()
        ids.mapNotNull { id ->
            val name = preferences[customSoundNameKey(id)] ?: return@mapNotNull null
            val uri = preferences[customSoundUriKey(id)] ?: return@mapNotNull null
            val icon = preferences[customSoundIconKey(id)] ?: "music_note"
            CustomSoundMetadata(id, name, uri, icon)
        }
    }

    val masterVolume: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[MASTER_VOLUME_KEY] ?: 1f
    }

    fun getSoundState(soundId: String): Flow<SoundState> = context.dataStore.data.map { preferences ->
        SoundState(
            soundId = soundId,
            isEnabled = preferences[soundEnabledKey(soundId)]
                ?: (soundId in DEFAULT_ENABLED_SOUNDS),
            volume = preferences[soundVolumeKey(soundId)]
                ?: DEFAULT_VOLUMES[soundId]
                ?: 0.45f
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

    suspend fun setMasterVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[MASTER_VOLUME_KEY] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun saveCustomSound(metadata: CustomSoundMetadata) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[CUSTOM_SOUND_IDS_KEY]?.toMutableSet() ?: mutableSetOf()
            currentIds.add(metadata.id)
            preferences[CUSTOM_SOUND_IDS_KEY] = currentIds
            preferences[customSoundNameKey(metadata.id)] = metadata.displayName
            preferences[customSoundUriKey(metadata.id)] = metadata.uriString
            preferences[customSoundIconKey(metadata.id)] = metadata.iconName
        }
    }

    suspend fun updateCustomSound(soundId: String, displayName: String, iconName: String) {
        context.dataStore.edit { preferences ->
            preferences[customSoundNameKey(soundId)] = displayName
            preferences[customSoundIconKey(soundId)] = iconName
        }
    }

    suspend fun removeCustomSound(soundId: String) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[CUSTOM_SOUND_IDS_KEY]?.toMutableSet() ?: mutableSetOf()
            currentIds.remove(soundId)
            preferences[CUSTOM_SOUND_IDS_KEY] = currentIds
            preferences.remove(customSoundNameKey(soundId))
            preferences.remove(customSoundUriKey(soundId))
            preferences.remove(customSoundIconKey(soundId))
            preferences.remove(soundEnabledKey(soundId))
            preferences.remove(soundVolumeKey(soundId))
        }
    }

    companion object {
        private val DEFAULT_ENABLED_SOUNDS = setOf("rain", "storm", "waves")
        private val DEFAULT_VOLUMES = mapOf(
            "rain" to 0.32f,
            "storm" to 0.52f,
            "waves" to 0.55f
        )
    }
}
