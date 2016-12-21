package com.github.tehras.workmode.ui.models

import com.google.android.gms.location.places.Place
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


class WorkPlace(place: Place) {

    @SerializedName("location")
    var location: WorkLocation? = null
    @SerializedName("address")
    var address: String = ""

    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "WorkPlace(location=$location, address=$address)"
    }

    companion object {
        fun fromJson(response: String): WorkPlace {
            return Gson().fromJson(response, WorkPlace::class.java)
        }
    }

    init {
        location = WorkLocation(place.latLng)
        address = place.address.toString()
    }


}