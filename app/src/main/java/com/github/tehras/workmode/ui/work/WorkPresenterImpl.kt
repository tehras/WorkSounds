package com.github.tehras.workmode.ui.work

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.github.tehras.workmode.shared.PreferenceSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.base.BaseActivity
import com.github.tehras.workmode.ui.dndpopup.DnDDialogFragment
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import javax.inject.Inject

class WorkPresenterImpl @Inject constructor(var preferences: SharedPreferences) : AbstractPresenter<WorkView>(), WorkPresenter {

    companion object {
        private var showedDndDialog = false
    }

    private var musicControls: AudioSettings? = null
    private var soundControls: AudioSettings? = null

    override fun isRingSettingDifferent(current: Int, max: Int): Boolean {
        Timber.d("isRingSettingDifferent $current, $max")

        return current != soundControls?.setMusicVolume ?: -1 || max != soundControls?.maxMusicVolume ?: -1
    }

    override fun isMusicSettingDifferent(current: Int, max: Int): Boolean {
        Timber.d("isMusicSettingDifferent $current, $max")

        return current != musicControls?.setMusicVolume ?: -1 || max != musicControls?.maxMusicVolume ?: -1
    }

    override fun updateRingSettings(current: Int, max: Int) {
        PreferenceSettings.saveToPreferences(AudioSettings(max, current), AudioType.RING, preferences)
    }

    override fun updateMusicSettings(current: Int, max: Int) {
        PreferenceSettings.saveToPreferences(AudioSettings(max, current), AudioType.MUSIC, preferences)
    }

    override fun checkForDnDOptions(activity: BaseActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!(activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                showDnDDialog(activity)
            }
        }

    }

    private fun showDnDDialog(activity: AppCompatActivity) {
        if (!showedDndDialog) {
            showedDndDialog = true

            DnDDialogFragment.instance().show(activity.supportFragmentManager, DnDDialogFragment::class.java.simpleName)
        }
    }

    override fun retrieveWorkTileSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //This api is only for 7.0+
            view?.hideTileCard()
        } else {
            val isSharedPreferencesEnabled = PreferenceSettings.isSharedPreferencesEnabled(preferences)
            view?.workTileStatus(isSharedPreferencesEnabled)
        }
    }

    override fun enableWorkTile(enable: Boolean) {
        PreferenceSettings.setSharedPreferencesEnabled(enable, preferences)
    }

    override fun retrieveSoundSettings() {
        Timber.d("retrieveSoundSettings - view $view")
        view?.let {
            Timber.d("retrieveSoundSettings - view $view")
            if (view is BaseActivity) {
                Timber.d("retrieveSoundSettings - music and ring")
                retrieveMusicControls(view as BaseActivity)
                retrieveRingControls(view as BaseActivity)
            }
        }
    }

    /**
     * Music Controls
     */
    private fun retrieveMusicControls(activity: BaseActivity) {
        musicControls = retrieveControls(AudioType.MUSIC, activity)

        view?.updateMusicControls(musicControls!!.setMusicVolume, musicControls!!.maxMusicVolume)
    }

    /**
     * Ring Controls
     */
    private fun retrieveRingControls(activity: BaseActivity) {
        soundControls = retrieveControls(AudioType.RING, activity)

        view?.updateRingControls(soundControls!!.setMusicVolume, soundControls!!.maxMusicVolume)
    }

    private fun retrieveControls(music: AudioType, activity: BaseActivity): AudioSettings {
        val maxMusicVolume: Int
        val currMusicVolume: Int
        val musicControls = PreferenceSettings.getPreferences(music, preferences)

        if (musicControls != null) {
            return musicControls
        } else {
            val audioManager: AudioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            currMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            val settings = AudioSettings(maxMusicVolume, currMusicVolume)

            when (music) {
                AudioType.MUSIC -> updateMusicSettings(currMusicVolume, maxMusicVolume)
                AudioType.RING -> updateRingSettings(currMusicVolume, maxMusicVolume)
            }

            return settings
        }
    }


    class AudioSettings(@SerializedName("maxMusicVolume") var maxMusicVolume: Int,
                        @SerializedName("setMusicVolume") var setMusicVolume: Int) {

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

    enum class AudioType { RING, MUSIC }
}