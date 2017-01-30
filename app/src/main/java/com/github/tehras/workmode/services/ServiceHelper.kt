package com.github.tehras.workmode.services

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import com.github.tehras.workmode.models.scene.AudioSetVolumePreference
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import timber.log.Timber

object ServiceHelper {
    fun enableScene(scene: ScenePreference, context: Context?, preference: SharedPreferences, postSoundChange: () -> Unit) {
        enableScene(scene, context, preference, true, postSoundChange)
    }

    fun enableScene(scene: ScenePreference, context: Context?, preference: SharedPreferences, showUi: Boolean, postSoundChange: () -> Unit) {
        Timber.d("trying to enable scene")
        context?.let {
            //record previous state
            //change settings
            Timber.d("enabling scene ${scene.name}")
            val audioManager: AudioManager = it.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            //get sound quality
            val currM = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
            val currR = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

            val music = scene.inMediaVolume
            val sound = scene.inRingVolume

            when (scene.outMediaPreferenceSelected) {
                AudioSetVolumePreference.BACK_TO_PREVIOUS -> {
                    //record current settings
                    scene.outMediaVolume = currM
                    scene.outRingVolume = currR

                    Timber.d("volume saved - $currM and $currR")
                    //update Scene
                    ScenePreferenceSettings.updateScene(scene, preference)
                }
                else -> {
                    //do nothing
                }
            }

            Timber.d("enableWork save - $music and $sound")

            if (music != null && sound != null && !isTheSameVolume(currM, currR, music, sound)) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music.setMusicVolume, 0)
                audioManager.setStreamVolume(AudioManager.STREAM_RING, sound.setMusicVolume, if (showUi) AudioManager.FLAG_SHOW_UI else 0)

                postSoundChange()
            }
        }
    }

    fun disableScene(scene: ScenePreference, context: Context?, postSoundChange: () -> Unit) {
        disableScene(scene, context, true, postSoundChange)
    }

    fun disableScene(scene: ScenePreference, context: Context?, showUi: Boolean, postSoundChange: () -> Unit) {
        context?.let {
            //get current system settings
            val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            //get sound quality
            val music = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
            val sound = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

            var sMusic: AudioSettings? = null
            var sRing: AudioSettings? = null

            when (scene.outMediaPreferenceSelected) {
                AudioSetVolumePreference.BACK_TO_PREVIOUS, AudioSetVolumePreference.CUSTOM -> {
                    sMusic = scene.outMediaVolume
                    sRing = scene.outRingVolume
                }
                AudioSetVolumePreference.DO_NOTHING -> {
                    sMusic = music
                    sRing = sound
                }
                else -> {
                    //leave as null
                }
            }

            Timber.d("isCurrentlyEnabled - ${sMusic?.setMusicVolume} and ${sRing?.setMusicVolume}")

            if (sMusic != null && sRing != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sRing.setMusicVolume, 0)
                audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing.setMusicVolume, if (showUi) AudioManager.FLAG_SHOW_UI else 0)

                postSoundChange()
            }
        }

    }

    private fun isTheSameVolume(currM: AudioSettings, currR: AudioSettings, music: AudioSettings, sound: AudioSettings): Boolean {
        return currM == music && currR == sound
    }
}