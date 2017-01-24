package com.github.tehras.workmode.models.scene

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VolumeLocation(location: LatLng) : Serializable {

    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null

    init {
        latitude = location.latitude
        longitude = location.longitude
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as VolumeLocation

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude?.hashCode() ?: 0
        result = 31 * result + (longitude?.hashCode() ?: 0)
        return result
    }


}