package com.github.tehras.workmode.ui.work

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.github.tehras.workmode.shared.LocationPreferenceSettings
import com.github.tehras.workmode.shared.TilePreferenceSettings
import com.github.tehras.workmode.ui.base.BaseActivity
import com.github.tehras.workmode.ui.dndpopup.DnDDialogFragment
import com.github.tehras.workmode.models.AudioSettings
import com.github.tehras.workmode.models.AudioType
import com.github.tehras.workmode.models.VolumePlace
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import timber.log.Timber
import javax.inject.Inject


class WorkPresenterImpl @Inject constructor(var preferences: SharedPreferences) : WorkFencePresenter(preferences), WorkPresenter {
    override fun initializeFenceApi() {
        super.initFenceApi()
    }

    /**
     * Location Text
     */
    override fun getLocationText(): CharSequence? {
        val location = LocationPreferenceSettings.getLocation(preferences)

        if (location != null) {
            return location.address
        } else {
            view?.showSetupLayout()
        }

        return ""
    }

    /**
     * Disable Location
     */
    override fun disableLocation() {
        LocationPreferenceSettings.deleteLocation(preferences)
        view?.showSetupLayout()
        view?.showDisabledLocation()
    }

    /**
     * Save Location Place
     */
    override fun saveLocationPlace(selectedPlace: Place?) {
        Timber.d("saveLocationPlace $selectedPlace")
        selectedPlace?.let {
            LocationPreferenceSettings.saveLocation(VolumePlace(it), preferences)
        }
    }

    /**
     * This will start the location Search Activity
     */
    override fun startLocationSearch() {
        val builder = PlacePicker.IntentBuilder()

        (view as WorkActivity).startActivityForResult(Intent(builder.build(view as BaseActivity)), PLACE_PICKER_REQUEST)
    }

    /**
     * This method will determine what to populate on the location based settings
     */
    override fun retrieveLocationBasedSettings() {
        val location = LocationPreferenceSettings.getLocation(preferences)

        Timber.d("Location is ${location?.toJson()}")
        if (location != null) {
            view?.showLocationEnabledLayout()
        } else {
            view?.showSetupLayout()
        }
    }

    companion object {
        val PLACE_PICKER_REQUEST = 1

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
        TilePreferenceSettings.saveToPreferences(AudioSettings(max, current), AudioType.RING, preferences)
    }

    override fun updateMusicSettings(current: Int, max: Int) {
        TilePreferenceSettings.saveToPreferences(AudioSettings(max, current), AudioType.MUSIC, preferences)
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
            val isSharedPreferencesEnabled = TilePreferenceSettings.isSharedPreferencesEnabled(preferences)
            view?.workTileStatus(isSharedPreferencesEnabled)
        }
    }

    override fun enableWorkTile(enable: Boolean) {
        TilePreferenceSettings.setSharedPreferencesEnabled(enable, preferences)
    }

    override fun retrieveSoundSettings() {
        view?.let {
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
        val musicControls = TilePreferenceSettings.getPreferences(music, preferences)

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

}