package com.github.tehras.workmode.services

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
import com.github.tehras.workmode.shared.PreferenceSettings
import com.github.tehras.workmode.ui.work.WorkPresenterImpl
import timber.log.Timber

class WorkSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!PreferenceSettings.isSharedPreferencesEnabled(getPreferences())) {
                qsTile.state = STATE_UNAVAILABLE
            }
        }
    }

    override fun onClick() {
        super.onClick()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(this, "OS version is too low", Toast.LENGTH_SHORT).show()
            return
        } else
            if (PreferenceSettings.isSharedPreferencesEnabled(getPreferences())) {
                when (qsTile.state) {
                    STATE_ACTIVE -> {
                        Timber.d("activated")
                        disableWork()
                        qsTile.state = STATE_INACTIVE
                    }
                    STATE_INACTIVE -> {
                        Timber.d("deactivated")
                        enableWork()
                        qsTile.state = STATE_ACTIVE
                    }
                }
                qsTile.icon = Icon.createWithResource(this.applicationContext, R.drawable.ic_work_tile)
                qsTile.updateTile()
            } else {
                qsTile.state = STATE_UNAVAILABLE
                Toast.makeText(this, "Please enable in ${this.resources.getText(R.string.app_name)}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enableWork() {
        //record previous state
        //change settings
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val music = WorkPresenterImpl.AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        val sound = WorkPresenterImpl.AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), audioManager.getStreamVolume(AudioManager.STREAM_RING))

        PreferenceSettings.saveLastState(music, sound, getPreferences())

        val sMusic = PreferenceSettings.getPreferences(WorkPresenterImpl.AudioType.MUSIC, getPreferences())
        val sRing = PreferenceSettings.getPreferences(WorkPresenterImpl.AudioType.RING, getPreferences())

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sMusic?.setMusicVolume ?: 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing?.setMusicVolume ?: 0, AudioManager.FLAG_SHOW_UI)
    }

    private fun disableWork() {
        //go back to previous state
        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val sMusic = PreferenceSettings.getLastStateMusic(getPreferences())
        val sRing = PreferenceSettings.getLastStateRing(getPreferences())

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sMusic?.setMusicVolume ?: 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, sRing?.setMusicVolume ?: 0, AudioManager.FLAG_SHOW_UI)
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }

}