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
}