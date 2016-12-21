package com.github.tehras.workmode.ui.work

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.ui.base.PresenterActivity
import com.github.tehras.workmode.ui.work.WorkPresenterImpl.Companion.PLACE_PICKER_REQUEST
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_work.*
import kotlinx.android.synthetic.main.partial_location_view.*
import kotlinx.android.synthetic.main.partial_tile_layout.*
import timber.log.Timber


class WorkActivity : PresenterActivity<WorkView, WorkPresenter>(), WorkView {
    override fun showDisabledLocation() {
        Snackbar.make(enable_location_layout, "Location based work mode disabled", Snackbar.LENGTH_LONG).show()
    }

    override fun showLocationEnabledLayout() {
        Timber.d("show location enabled Layout")
        location_layout.visibility = View.VISIBLE
        setup_layout.visibility = View.GONE
        location_based_location.text = presenter.getLocationText()

        location_based_edit_btn.setButtonColor(android.R.color.white)
        location_based_edit_btn.setOnClickListener { presenter.startLocationSearch() }
        location_based_disable.setButtonColor(android.R.color.white)
        location_based_disable.setOnClickListener { presenter.disableLocation() }
    }

    override fun showSetupLayout() {
        Timber.d("show setup layout")
        location_layout.visibility = View.GONE
        setup_layout.visibility = View.VISIBLE
        location_based_button.setButtonColor(R.color.colorAccent)
        location_based_button.setOnClickListener { presenter.startLocationSearch() }
    }

    override fun updateMusicControls(currMusicVolume: Int, maxMusicVolume: Int) {
        seek_bar_sound.max = maxMusicVolume
        seek_bar_sound.progress = currMusicVolume
    }

    override fun updateRingControls(currMusicVolume: Int, maxMusicVolume: Int) {
        seek_bar_ring.max = maxMusicVolume
        seek_bar_ring.progress = currMusicVolume
    }

    override fun hideTileCard() {
        enable_location_layout.visibility = View.GONE
    }

    override fun workTileStatus(isEnabled: Boolean) {
        Timber.d("work tile status - $isEnabled")
        updateButtonStatus(isEnabled)
        showVolumeSettings(isEnabled)
    }

    private fun updateButtonStatus(isEnabled: Boolean) {
        Timber.d("updateButtonStatus $isEnabled")
        if (!isEnabled) {
            enabledButton()
        } else {
            disabledButton()
        }
    }

    private fun showVolumeSettings(enabled: Boolean) {
        Timber.d("showVolumeSettings enabled - $enabled")
        presenter.enableWorkTile(enabled)

        if (enabled) {
            presenter.retrieveSoundSettings()
            work_tile_volume_settings.visibility = View.VISIBLE
        } else {
            work_tile_volume_settings.visibility = View.GONE
        }
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(WorkModule(this))
                .injectTo(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_work)
    }

    private fun enabledButton() {
        enable_tile_button.text = getString(R.string.enable_text)
        enable_tile_button.setTextColor(Color.WHITE)
        enable_tile_button.setButtonColor(R.color.colorAccent)
    }

    private fun disabledButton() {
        @Suppress("DEPRECATION")
        enable_tile_button.setTextColor(Color.DKGRAY)
        enable_tile_button.text = getString(R.string.disable_text)
        enable_tile_button.setButtonColor(android.R.color.white)
    }

    override fun onStart() {
        super.onStart()

        presenter.retrieveWorkTileSettings()
        presenter.retrieveLocationBasedSettings()

        presenter.initializeFenceApi()

        enable_tile_button.setOnClickListener {
            val b: Boolean
            if (enable_tile_button.text.toString() == "Enable") {
                presenter.checkForDnDOptions(this@WorkActivity)
                b = false
            } else {
                b = true
            }
            updateButtonStatus(!b)
            showVolumeSettings(!b)
            work_tile_update.isEnabled = false
        }

        work_tile_update.setOnClickListener {
            presenter.updateMusicSettings(seek_bar_sound.progress, seek_bar_sound.max)
            presenter.updateRingSettings(seek_bar_ring.progress, seek_bar_ring.max)
            enableEnableButton(false)
            Snackbar.make(it, "Tile settings updated!", Snackbar.LENGTH_SHORT).show()
        }

        enableEnableButton(false)
        seek_bar_ring.setOnSeekBarChangeListener(SoundSeekBarListener({ enableEnableButton(presenter.isRingSettingDifferent(this.progress, this.max)) }))
        seek_bar_sound.setOnSeekBarChangeListener(SoundSeekBarListener({ enableEnableButton(presenter.isMusicSettingDifferent(this.progress, this.max)) }))
    }

    @Suppress("DEPRECATION")
    private fun enableEnableButton(isEnabled: Boolean) {
        Timber.d("enableEnableButton $isEnabled")
        work_tile_update.isEnabled = isEnabled
        if (isEnabled) {
            work_tile_update.setButtonColor(R.color.colorAccent)
            work_tile_update.setTextColor(resources.getColor(android.R.color.white))
        } else {
            work_tile_update.setTextColor(resources.getColor(android.R.color.darker_gray))
            work_tile_update.setButtonColor(android.R.color.white)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult $requestCode, $requestCode, $data")
        if (requestCode === PLACE_PICKER_REQUEST) {
            if (resultCode === Activity.RESULT_OK || resultCode === Activity.RESULT_FIRST_USER) {
                val selectedPlace = PlacePicker.getPlace(this, data)
                Timber.d("selectedPlace - $selectedPlace")

                //save the place
                presenter.saveLocationPlace(selectedPlace)
                showLocationEnabledLayout()
            }
        }
    }

}
