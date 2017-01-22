package com.github.tehras.workmode.models.scene

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScenePreference : Serializable {

    @SerializedName("name")
    var name: String? = ""
    @SerializedName("inMediaVolume")
    var inMediaVolume: AudioSettings? = null
    @SerializedName("inRingVolume")
    var inRingVolume: AudioSettings? = null

    @SerializedName("outMediaPreference")
    var outMediaPreferenceSelected: AudioSetVolumePreference = AudioSetVolumePreference.BACK_TO_PREVIOUS
    @SerializedName("outMediaVolume")
    var outMediaVolume: AudioSettings? = null
    @SerializedName("outRingVolume")
    var outRingVolume: AudioSettings? = null

    @SerializedName("location")
    var location: VolumePlace? = null

    @SerializedName("selectedTile")
    var selectedTile: TileImage = TileImage.NONE

    fun toJson(): String {
        return Gson().toJson(this@ScenePreference)
    }

}