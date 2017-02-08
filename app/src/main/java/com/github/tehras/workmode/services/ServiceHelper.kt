package com.github.tehras.workmode.services

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import com.github.tehras.workmode.models.scene.AudioSetVolumePreference
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import timber.log.Timber


object ServiceHelper {
    fun enableScene(scene: ScenePreference, context: Context?, preference: SharedPreferences, postSoundChange: () -> Unit) {
        enableScene(scene, context, preference, true, postSoundChange)
    }

    // called to send data to Activity
    fun Context.soundUpdated() {
        Timber.d("broadcastActionChanged")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intent = Intent(PreferenceBaseTileService.BROADCAST_ACTION_REFRESH)
            val bm = LocalBroadcastManager.getInstance(this)
            bm.sendBroadcast(intent)
        }
    }

    fun enableScene(scene: ScenePreference, context: Context?, preference: SharedPreferences, showUi: Boolean, postSoundChange: () -> Unit) {
        Timber.d("trying to enable scene")
        context?.let {
            //record previous state
            //change settings
            Timber.d("enabling scene ${scene.name}")

            var audioManager: AudioManager? = null
            if (scene.isMediaEnabled() || scene.isRingEnabled())
                audioManager = it.getSystemService(Context.AUDIO_SERVICE) as AudioManager?


            if (scene.isMediaEnabled()) {
                //get sound quality
                val currM = AudioSettings(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                val music = scene.inMediaVolume

                when (scene.outMediaPreferenceSelected) {
                    AudioSetVolumePreference.BACK_TO_PREVIOUS -> {
                        //record current settings
                        scene.outMediaVolume = currM

                        Timber.d("media saved - $currM")
                        //update Scene
                        ScenePreferenceSettings.updateScene(scene, preference)
                    }
                    else -> {
                        //do nothing
                    }
                }

                Timber.d("enable media save - $music")

                if (music != null && !isTheSameVolume(currM, music)) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music.setMusicVolume, if (showUi && !scene.isRingEnabled()) AudioManager.FLAG_SHOW_UI else 0)
                }
            }

            if (scene.isRingEnabled()) {
                val currR = AudioSettings(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))
                val sound = scene.inRingVolume

                when (scene.outMediaPreferenceSelected) {
                    AudioSetVolumePreference.BACK_TO_PREVIOUS -> {
                        //record current settings
                        scene.outRingVolume = currR

                        Timber.d("ring saved - $currR")
                        //update Scene
                        ScenePreferenceSettings.updateScene(scene, preference)
                    }
                    else -> {
                        //do nothing
                    }
                }

                Timber.d("enable ring save - $sound")

                if (sound != null && !isTheSameVolume(currR, sound)) {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, sound.setMusicVolume, if (showUi) AudioManager.FLAG_SHOW_UI else 0)
                }
            }

            if (scene.wifiEnabled) {
                //wifi state
                Timber.i("wifi is being ${scene.wifiState}")
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

                scene.wifiEnterState = wifiManager.isWifiEnabled

                wifiManager.isWifiEnabled = scene.wifiState
            }

            postSoundChange()
        }
    }

    fun disableScene(scene: ScenePreference, context: Context?, postSoundChange: () -> Unit) {
        disableScene(scene, context, true, postSoundChange)
    }

    fun disableScene(scene: ScenePreference, context: Context?, showUi: Boolean, postSoundChange: () -> Unit) {
        context?.let {
            var audioManager: AudioManager? = null

            if (scene.isRingEnabled() || scene.isMediaEnabled())
                audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

            if (scene.isMediaEnabled()) {
                val sMusic: AudioSettings? = scene.outMediaVolume

                if (sMusic != null) {
                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, sMusic.setMusicVolume, if (showUi && !scene.isRingEnabled()) AudioManager.FLAG_SHOW_UI else 0)
                }
            }
            if (scene.isRingEnabled()) {
                val sRing: AudioSettings? = scene.outRingVolume

                if (sRing != null) {
                    audioManager!!.setStreamVolume(AudioManager.STREAM_RING, sRing.setMusicVolume, if (showUi) AudioManager.FLAG_SHOW_UI else 0)
                }
            }
            if (scene.wifiEnabled) {
                //wifi state
                Timber.i("wifi is being ${scene.wifiState}")
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.isWifiEnabled = scene.wifiEnterState
            }

            postSoundChange()
        }

    }

    fun isCurrentlyEnabled(context: Context?, scene: ScenePreference): Boolean {
        var ringMatched: Boolean = true
        var mediaMatched: Boolean = true
        var wifiMatches: Boolean = true

        var audioManager: AudioManager? = null
        if (scene.isRingEnabled() || scene.isMediaEnabled())
            audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        if (scene.isMediaEnabled()) {
            val music = AudioSettings(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
            val sMusic = scene.inMediaVolume

            mediaMatched = (sMusic?.setMusicVolume == music.setMusicVolume)
        }

        if (scene.isRingEnabled()) {
            //get sound quality
            val sound = AudioSettings(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))
            val sRing = scene.inRingVolume

            ringMatched = (sRing?.setMusicVolume == sound.setMusicVolume)
        }

        if (scene.wifiEnabled) {
            val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager

            wifiMatches = wifiManager.isWifiEnabled == scene.wifiState
        }

        return ringMatched && mediaMatched && wifiMatches
    }


    private fun isTheSameVolume(curr: AudioSettings, setting: AudioSettings): Boolean {
        return curr == setting
    }
}