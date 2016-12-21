package com.github.tehras.workmode.services

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.preference.PreferenceManager
import android.service.quicksettings.Tile.*
import android.service.quicksettings.TileService
import android.widget.Toast
import com.github.tehras.workmode.R
import com.github.tehras.workmode.shared.TilePreferenceSettings
import com.github.tehras.workmode.ui.models.AudioSettings
import com.github.tehras.workmode.ui.models.AudioType
import timber.log.Timber


class WorkSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!TilePreferenceSettings.isSharedPreferencesEnabled(getPreferences())) {
                qsTile.state = STATE_UNAVAILABLE
                qsTile.updateTile()
            } else {
                if (isWorkMode()) qsTile.state = STATE_ACTIVE else qsTile.state = STATE_INACTIVE
                qsTile.updateTile()
            }
        }
    }

    override fun onClick() {
        super.onClick()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(this, "OS version is too low", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                Toast.makeText(this, "Cannot change due to DND mode", Toast.LENGTH_SHORT).show()
            } else if (TilePreferenceSettings.isSharedPreferencesEnabled(getPreferences())) {
                when (qsTile.state) {
                    STATE_ACTIVE -> {
                        Timber.d("activated")
                        Toast.makeText(this, "Work mode disabled!", Toast.LENGTH_SHORT).show()
                        disableWork()
                        qsTile.state = STATE_INACTIVE
                    }
                    STATE_INACTIVE -> {
                        Timber.d("deactivated")
                        Toast.makeText(this, "Work mode enabled!", Toast.LENGTH_SHORT).show()
                        enableWork()
                        qsTile.state = STATE_ACTIVE
                    }
                    STATE_UNAVAILABLE -> {
                        Timber.d("unavailable")
                        this.startActivity(android.content.Intent(this, com.github.tehras.workmode.ui.work.WorkActivity::class.java))
                    }
                }
                qsTile.icon = Icon.createWithResource(this.applicationContext, R.drawable.ic_work_tile)
                qsTile.updateTile()
            } else {
                qsTile.state = STATE_UNAVAILABLE
                Toast.makeText(this, "Please enable in ${this.resources.getText(R.string.app_name)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableWork() {
        //record previous state
        //change settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val music = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        val sound = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

        Timber.d("enableWork save - $music and $sound")
        TilePreferenceSettings.saveLastState(music, sound, getPreferences())

        val sMusic = TilePreferenceSettings.getPreferences(AudioType.MUSIC, getPreferences())
        val sRing = TilePreferenceSettings.getPreferences(AudioType.RING, getPreferences())

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sMusic?.setMusicVolume ?: 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing?.setMusicVolume ?: 0, AudioManager.FLAG_SHOW_UI)
    }

    private fun disableWork() {
        //go back to previous state
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val sMusic = TilePreferenceSettings.getLastStateMusic(getPreferences())
        val sRing = TilePreferenceSettings.getLastStateRing(getPreferences())

        val tMusic = TilePreferenceSettings.getPreferences(AudioType.MUSIC, getPreferences())
        val tRing = TilePreferenceSettings.getPreferences(AudioType.RING, getPreferences())

        Timber.d("disable work - $sMusic and $sRing")
        if (sMusic?.equals(tMusic) ?: false && sRing?.equals(tRing) ?: false) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sMusic?.setMusicVolume ?: 0, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing?.setMusicVolume ?: 0, AudioManager.FLAG_SHOW_UI)
        }
    }

    private fun isWorkMode(): Boolean {
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val music = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        val sound = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

        val sMusic = TilePreferenceSettings.getPreferences(AudioType.MUSIC, getPreferences())
        val sRing = TilePreferenceSettings.getPreferences(AudioType.RING, getPreferences())

        Timber.d("isWorkMode - ${sMusic?.setMusicVolume} and ${sRing?.setMusicVolume}")

        return (sMusic?.setMusicVolume == music.setMusicVolume) && (sRing?.setMusicVolume == sound.setMusicVolume)
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }

}