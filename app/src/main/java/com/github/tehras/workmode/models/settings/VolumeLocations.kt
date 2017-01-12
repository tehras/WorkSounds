package com.github.tehras.workmode.models.settings

import com.github.tehras.workmode.models.VolumeLocation
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class VolumeLocations(
        @SerializedName("volumeLocations")
        var locations: ArrayList<VolumeLocation>) : Serializable