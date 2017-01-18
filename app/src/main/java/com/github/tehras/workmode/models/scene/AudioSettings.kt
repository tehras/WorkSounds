package com.github.tehras.workmode.models.scene

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AudioSettings(@SerializedName("maxMusicVolume") var maxMusicVolume: Int,
                         @SerializedName("setMusicVolume") var setMusicVolume: Int) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AudioSettings

        if (maxMusicVolume != other.maxMusicVolume) return false
        if (setMusicVolume != other.setMusicVolume) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxMusicVolume
        result = 31 * result + setMusicVolume
        return result
    }

    override fun toString(): String {
        return "AudioSettings(maxMusicVolume=$maxMusicVolume, setMusicVolume=$setMusicVolume)"
    }

    companion object {

        fun fromJson(string: String): AudioSettings {
            return Gson().fromJson(string, AudioSettings::class.java)
        }
    }
}