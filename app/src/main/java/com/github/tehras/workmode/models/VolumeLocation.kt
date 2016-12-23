package com.github.tehras.workmode.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

class VolumeLocation {

    constructor(location: LatLng) {
        latitude = location.latitude
        longitude = location.longitude
    }

    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null
}