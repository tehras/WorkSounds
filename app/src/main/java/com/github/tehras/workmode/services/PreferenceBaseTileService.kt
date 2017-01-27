package com.github.tehras.workmode.services

import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.github.tehras.workmode.models.scene.AudioSetVolumePreference
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.splashscreen.SplashScreen
import timber.log.Timber
import java.util.*


abstract class PreferenceBaseTileService : TileService() {
    companion object {
        val BROADCAST_ACTION_REFRESH = "com.github.tehras.broadcast.refresh"
    }

    // handler for received data from service
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("onReceive")
            if (intent.action == BROADCAST_ACTION_REFRESH) {
                tileAddedOrStartListening()
            }
        }
    }

    // called to send data to Activity
    fun soundUpdated() {
        Timber.d("broadcastActionChanged")
        val intent = Intent(BROADCAST_ACTION_REFRESH)
        val bm = LocalBroadcastManager.getInstance(this)
        bm.sendBroadcast(intent)
    }

    override fun onStopListening() {
        val bm = LocalBroadcastManager.getInstance(this)
        bm.unregisterReceiver(mBroadcastReceiver)

        super.onStopListening()
    }

    override fun onStartListening() {
        super.onStartListening()

        tileAddedOrStartListening()

        val filter = IntentFilter()
        filter.addAction(BROADCAST_ACTION_REFRESH)
        val bm = LocalBroadcastManager.getInstance(this)
        bm.registerReceiver(mBroadcastReceiver, filter)
    }

    private fun tileAddedOrStartListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //check if there's more than 1 service
            val scene = getCorrectScene()

            Timber.d("scene - $scene")
            if (scene != null) {
                //set active or inactive
                if (isCurrentlyEnabled(scene))
                    qsTile.state = Tile.STATE_ACTIVE
                else qsTile.state = Tile.STATE_INACTIVE
            } else {
                qsTile.state = Tile.STATE_UNAVAILABLE
            }
            qsTile.updateTile()
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()

        tileAddedOrStartListening()
    }

    private fun getCorrectScene(): ScenePreference? {
        return getScene(ScenePreferenceSettings.getAllScenes(getPreferences()))
    }

    abstract fun getScene(scenes: ArrayList<ScenePreference>): ScenePreference?

    override fun onClick() {
        super.onClick()

        val scene = getCorrectScene()

        Timber.d("onClick - $scene")

        scene?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return
            } else {
                if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                    Toast.makeText(this, "Cannot change due to DND mode", Toast.LENGTH_SHORT).show()
                    //start app with permissions
                    startSplashScreen()
                } else {
                    when (qsTile.state) {
                        Tile.STATE_ACTIVE -> {
                            Timber.d("activated")
                            disableScene(it)
                            qsTile.state = Tile.STATE_INACTIVE
                        }
                        Tile.STATE_INACTIVE -> {
                            Timber.d("deactivated")
                            ServiceHelper.enableScene(it, this, getPreferences()) {
                                soundUpdated()
                            }
                            qsTile.state = Tile.STATE_ACTIVE
                        }
                        Tile.STATE_UNAVAILABLE -> {
                            Timber.d("unavailable")
                            startSplashScreen()
                        }
                    }

                    qsTile.label = scene.name
                    qsTile.icon = Icon.createWithResource(this.applicationContext, it.selectedTile.tile)

                    qsTile.updateTile()
                }
            }
        }

        if (scene == null) {
            startSplashScreen()
        }
    }

    private fun startSplashScreen() {
        this.startActivity(android.content.Intent(this, SplashScreen::class.java)) //add extra
    }

    private fun isCurrentlyEnabled(scene: ScenePreference): Boolean {
        //get current system settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //get sound quality
        val music = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        val sound = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

        val sMusic = scene.inMediaVolume
        val sRing = scene.inRingVolume

        Timber.d("isCurrentlyEnabled - ${sMusic?.setMusicVolume} and ${sRing?.setMusicVolume} = ${(sMusic?.setMusicVolume == music.setMusicVolume) && (sRing?.setMusicVolume == sound.setMusicVolume)}")

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
            audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing.setMusicVolume, AudioManager.FLAG_SHOW_UI)
        }

    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }
}