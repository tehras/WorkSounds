package com.github.tehras.workmode.shared

import android.content.SharedPreferences
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.AudioType
import timber.log.Timber

object TilePreferenceSettings {
    val KEY_SETTINGS_ENABLED = "key_settings_enabled"
    val KEY_RING_TYPE = "key_settings_ring_type"
    val KEY_MUSIC_TYPE = "key_settings_music_type"
    val KEY_LAST_STATE_MUSIC = "key_last_state_music"
    val KEY_LAST_STATE_RING = "key_last_state_ring"

    fun saveLastState(music: AudioSettings, ring: AudioSettings, preferences: SharedPreferences) {
        preferences.edit().putString(KEY_LAST_STATE_MUSIC, music.toJson()).apply()
        preferences.edit().putString(KEY_LAST_STATE_RING, ring.toJson()).apply()
    }

    fun getLastStateRing(preferences: SharedPreferences): AudioSettings? {
        val response = preferences.getString(KEY_LAST_STATE_RING, "")
        if (response.isNullOrBlank())
            return null

        return AudioSettings.fromJson(response)
    }

    fun getLastStateMusic(preferences: SharedPreferences): AudioSettings? {
        val response = preferences.getString(KEY_LAST_STATE_MUSIC, "")
        if (response.isNullOrBlank())
            return null

        return AudioSettings.fromJson(response)
    }

    fun isSharedPreferencesEnabled(preferences: SharedPreferences): Boolean {
        return preferences.getBoolean(KEY_SETTINGS_ENABLED, false)
    }

    fun setSharedPreferencesEnabled(enable: Boolean, preferences: SharedPreferences) {
        Timber.d("setSharedPreferencesEnabled $enable")
        if (!enable) {
            preferences.edit().clear().apply()
        } else {
            preferences.edit().putBoolean(KEY_SETTINGS_ENABLED, enable).apply()
        }
    }

    fun saveToPreferences(audioSettings: AudioSettings, soundType: AudioType, preferences: SharedPreferences) {
        val preferenceKey = getPreferenceKey(soundType)

        preferences.edit().putString(preferenceKey, audioSettings.toJson()).apply()
    }

    fun getPreferences(soundType: AudioType, preferences: SharedPreferences): AudioSettings? {
        val response = preferences.getString(getPreferenceKey(soundType), "")
        if (response.isNullOrBlank())
            return null

        return AudioSettings.fromJson(response)
    }

    private fun getPreferenceKey(soundType: AudioType): String {
        when (soundType) {
            AudioType.RING -> {
                return KEY_RING_TYPE
            }
            AudioType.MUSIC -> {
                return KEY_MUSIC_TYPE
            }
        }
    }

}