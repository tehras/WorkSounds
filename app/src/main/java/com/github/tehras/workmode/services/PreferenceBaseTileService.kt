package com.github.tehras.workmode.services

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.scene.AudioSetVolumePreference
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import timber.log.Timber
import java.util.*

abstract class PreferenceBaseTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //check if there's more than 1 service
            val scene = getCorrectScene()

            if (scene != null) {
                //set active or inactive
            } else {
                qsTile.state = Tile.STATE_UNAVAILABLE
                qsTile.updateTile()
            }
        }
    }

    private fun getCorrectScene(): ScenePreference? {
        return getScene(ScenePreferenceSettings.getAllScenes(getPreferences()))
    }

    abstract fun getScene(scenes: ArrayList<ScenePreference>): ScenePreference?

    override fun onClick() {
        super.onClick()

        val scene = getCorrectScene()
        scene?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Toast.makeText(this, "OS version is too low", Toast.LENGTH_SHORT).show()
                return
            } else {
                if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                    Toast.makeText(this, "Cannot change due to DND mode", Toast.LENGTH_SHORT).show()
                } else if (isCurrentlyEnabled(scene)) {
                    when (qsTile.state) {
                        Tile.STATE_ACTIVE -> {
                            Timber.d("activated")
                            Toast.makeText(this, "Work mode disabled!", Toast.LENGTH_SHORT).show()
                            enableScene(it)
                            qsTile.state = Tile.STATE_INACTIVE
                        }
                        Tile.STATE_INACTIVE -> {
                            Timber.d("deactivated")
                            Toast.makeText(this, "Work mode enabled!", Toast.LENGTH_SHORT).show()
                            disableScene(it)
                            qsTile.state = Tile.STATE_ACTIVE
                        }
                        Tile.STATE_UNAVAILABLE -> {
                            Timber.d("unavailable")
                            this.startActivity(android.content.Intent(this, com.github.tehras.workmode.ui.work.WorkActivity::class.java))
                        }
                    }

                    qsTile.label = scene.name
                    qsTile.icon = Icon.createWithResource(this.applicationContext, it.selectedTile.tile)

                    qsTile.updateTile()
                } else {
                    qsTile.state = Tile.STATE_UNAVAILABLE
                    Toast.makeText(this, "Please enable in ${this.resources.getText(R.string.app_name)}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isCurrentlyEnabled(scene: ScenePreference): Boolean {
        //get current system settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //get sound quality
        val music = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        val sound = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

        val sMusic = scene.inMediaVolume
        val sRing = scene.inRingVolume

        Timber.d("isCurrentlyEnabled - ${sMusic?.setMusicVolume} and ${sRing?.setMusicVolume}")

        return (sMusic?.setMusicVolume == music.setMusicVolume) && (sRing?.setMusicVolume == sound.setMusicVolume)
    }

    private fun disableScene(scene: ScenePreference) {
        //get current system settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

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
            audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing.setMusicVolume, 0)
        }

    }

    private fun enableScene(scene: ScenePreference) {
        //record previous state
        //change settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

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
                ScenePreferenceSettings.updateScene(scene, getPreferences())
            }
            else -> {
                //do nothing
            }
        }

        Timber.d("enableWork save - $music and $sound")

        if (music != null && sound != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music.setMusicVolume, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, music.setMusicVolume, 0)
        }
    }


    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }
}