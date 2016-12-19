package com.github.tehras.workmode.ui.work

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.ui.base.PresenterActivity
import kotlinx.android.synthetic.main.activity_work.*
import timber.log.Timber

class WorkActivity : PresenterActivity<WorkView, WorkPresenter>(), WorkView {

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
        enable_tile_switch.isChecked = isEnabled

        showVolumeSettings(isEnabled)
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

    override fun onStart() {
        super.onStart()

        presenter.retrieveWorkTileSettings()
        enable_tile_switch.setOnCheckedChangeListener { compoundButton, b ->
            showVolumeSettings(b)
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

}
