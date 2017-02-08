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

    @SerializedName("wifiEnabled")
    var wifiEnabled: Boolean = false
    @SerializedName("wifiState")
    var wifiState: Boolean = true
    @SerializedName("wifiEnterState")
    var wifiEnterState: Boolean = true

    fun isMediaEnabled(): Boolean {
        return inMediaVolume != null
    }

    fun isRingEnabled(): Boolean {
        return inRingVolume != null
    }

    fun update(scene: ScenePreference) {
        this.name = scene.name
        this.inMediaVolume = scene.inMediaVolume
        this.inRingVolume = scene.inRingVolume

        this.outMediaPreferenceSelected = scene.outMediaPreferenceSelected
        this.outMediaVolume = scene.outMediaVolume
        this.outRingVolume = scene.outRingVolume

        this.location = scene.location
        this.selectedTile = scene.selectedTile

        this.wifiEnabled = scene.wifiEnabled
        this.wifiState = scene.wifiState
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

    override fun toString(): String {
        return "ScenePreference(id=$id, name=$name)"
    }


}