package com.github.tehras.workmode.models.generalsettings

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class GeneralSettings(
        @SerializedName("enableNotifications")
        var enableNotifications: Boolean,
        @SerializedName("locationRange")
        var locationRange: Int
)

private val GENERAL_SETTINGS_STRING = "arg_general_settings_string"

fun getGeneralSettings(preferences: SharedPreferences): GeneralSettings {
    val settings = preferences.getString(GENERAL_SETTINGS_STRING, "")

    return parseObject(s = settings, preferences = preferences)
}

fun updateGeneralSettings(generalSettings: GeneralSettings?, preferences: SharedPreferences) {
    generalSettings?.let {
        preferences.edit().putString(GENERAL_SETTINGS_STRING, convertToJson(generalSettings)).apply()
    }
}

private fun parseObject(s: String, preferences: SharedPreferences): GeneralSettings {
    var generalSettings = convertFromJson(s)

    if (generalSettings == null) {
        //save one
        generalSettings = GeneralSettings(true, 200)
        updateGeneralSettings(generalSettings, preferences)
        return generalSettings
    }

    return generalSettings
}


fun convertToJson(generalSettings: GeneralSettings): String? {
    return Gson().toJson(generalSettings)
}

fun convertFromJson(s: String): GeneralSettings? {
    try {
        return Gson().fromJson(s, GeneralSettings::class.java)
    } catch (e: Exception) {
        return null
    }
}
