package com.github.tehras.workmode.models.settings

import android.support.annotation.IntegerRes
import com.github.tehras.workmode.models.AudioSettings
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VolumeSettingGroup(
        @SerializedName("name")
        val name: String,
        @SerializedName("ringSetting")
        val ringSetting: AudioSettings,
        @SerializedName("audioSettings")
        val mediaSetting: AudioSettings,
        @SerializedName("volumeLocations")
        val volumeLocations: VolumeLocations,
        @SerializedName("image")
        @IntegerRes
        val image: Int) : Serializable