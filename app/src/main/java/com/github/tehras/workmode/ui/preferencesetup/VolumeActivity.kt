package com.github.tehras.workmode.ui.preferencesetup

import android.os.Bundle
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.getLastFragmentInStack
import com.github.tehras.workmode.extensions.startFragment
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.PresenterActivity
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsFragment
import kotlinx.android.synthetic.main.activity_volume.*
import timber.log.Timber

class VolumeActivity : PresenterActivity<VolumeView, VolumePresenter>(), VolumeView {
    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeModule(this)).injectTo(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume)
    }

    fun onBackPressedIgnoreOverride() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        val fragment = this.getLastFragmentInStack()
        Timber.d("fragment - $fragment")
        if (fragment !is VolumeNewSettingsFragment || fragment.alreadyShowedCancelMessage()) {
            super.onBackPressed()
        } else if (!fragment.alreadyShowedCancelMessage()) {
            fragment.showCancelDialog()
        }
    }

    fun showNewVolumeFragment(volumeGroup: VolumeSettingGroup?) {
        VolumeNewSettingsFragment.instance(volumeGroup).startFragment(this, new_scene_view_container, true)
    }

}
