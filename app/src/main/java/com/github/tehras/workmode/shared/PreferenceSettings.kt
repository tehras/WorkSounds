package com.github.tehras.workmode.shared

import android.content.SharedPreferences
import com.github.tehras.workmode.ui.work.WorkPresenterImpl
import timber.log.Timber

object PreferenceSettings {
    val KEY_SETTINGS_ENABLED = "key_settings_enabled"
    val KEY_RING_TYPE = "key_settings_ring_type"
    val KEY_MUSIC_TYPE = "key_settings_music_type"
    val KEY_LAST_STATE_MUSIC = "key_last_state_music"
    val KEY_LAST_STATE_RING = "key_last_state_ring"

    fun saveLastState(music: WorkPresenterImpl.AudioSettings, ring: WorkPresenterImpl.AudioSettings, preferences: SharedPreferences) {
        preferences.edit().putString(KEY_LAST_STATE_MUSIC, music.toJson()).apply()
        preferences.edit().putString(KEY_LAST_STATE_RING, ring.toJson()).apply()
    }

    fun getLastStateRing(preferences: SharedPreferences): WorkPresenterImpl.AudioSettings? {
        val response = preferences.getString(KEY_LAST_STATE_RING, "")
        if (response.isNullOrBlank())
            return null

        return WorkPresenterImpl.AudioSettings.fromJson(response)
    }

    fun getLastStateMusic(preferences: SharedPreferences): WorkPresenterImpl.AudioSettings? {
        val response = preferences.getString(KEY_LAST_STATE_MUSIC, "")
        if (response.isNullOrBlank())
            return null

        return WorkPresenterImpl.AudioSettings.fromJson(response)
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

    fun saveToPreferences(audioSettings: WorkPresenterImpl.AudioSettings, soundType: WorkPresenterImpl.AudioType, preferences: SharedPreferences) {
        val preferenceKey = getPreferenceKey(soundType)

        preferences.edit().putString(preferenceKey, audioSettings.toJson()).apply()
    }

    fun getPreferences(soundType: WorkPresenterImpl.AudioType, preferences: SharedPreferences): WorkPresenterImpl.AudioSettings? {
        val response = preferences.getString(getPreferenceKey(soundType), "")
        if (response.isNullOrBlank())
            return null

        return WorkPresenterImpl.AudioSettings.fromJson(response)
    }

    private fun getPreferenceKey(soundType: WorkPresenterImpl.AudioType): String {
        when (soundType) {
            WorkPresenterImpl.AudioType.RING -> {
                return KEY_RING_TYPE
            }
            WorkPresenterImpl.AudioType.MUSIC -> {
                return KEY_MUSIC_TYPE
            }
        }
    }

}