package com.github.tehras.workmode.models.scene

import com.google.android.gms.location.places.Place
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VolumePlace(place: Place) : Serializable {

    @SerializedName("location")
    var location: VolumeLocation? = null
    @SerializedName("address")
    var address: String = ""

    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "VolumePlace(location=$location, address=$address)"
    }

    companion object {
        fun fromJson(response: String): VolumePlace {
            return Gson().fromJson(response, VolumePlace::class.java)
        }
    }

    init {
        location = VolumeLocation(place.latLng)
        address = place.address.toString()
    }


}