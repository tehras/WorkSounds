package com.github.tehras.workmode.shared

import android.content.SharedPreferences
import com.github.tehras.workmode.ui.models.VolumePlace
import timber.log.Timber

object LocationPreferenceSettings {
    val KEY_LOCATION_SAVED = "key_location_saved"

    fun saveLocation(location: VolumePlace, preferences: SharedPreferences) {
        Timber.d("saving location - $location")
        preferences.edit().putString(KEY_LOCATION_SAVED, location.toJson()).apply()
    }

    fun getLocation(preferences: SharedPreferences): VolumePlace? {
        val response = preferences.getString(KEY_LOCATION_SAVED, "")

        if (response.isNullOrBlank())
            return null

        return VolumePlace.fromJson(response)
    }

    fun deleteLocation(preferences: SharedPreferences) {
        preferences.edit().putString(KEY_LOCATION_SAVED, "").apply()
    }
}