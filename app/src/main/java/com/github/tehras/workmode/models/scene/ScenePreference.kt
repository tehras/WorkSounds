package com.github.tehras.workmode.models.scene

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class ScenePreference : Serializable {

    @SerializedName("UUID")
    val id: UUID = UUID.randomUUID()

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

    fun update(scene: ScenePreference) {
        this.name = scene.name
        this.inMediaVolume = scene.inMediaVolume
        this.inRingVolume = scene.inRingVolume

        this.outMediaPreferenceSelected = scene.outMediaPreferenceSelected
        this.outMediaVolume = scene.outMediaVolume
        this.outRingVolume = scene.outRingVolume

        this.location = scene.location
        this.selectedTile = scene.selectedTile
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ScenePreference

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}