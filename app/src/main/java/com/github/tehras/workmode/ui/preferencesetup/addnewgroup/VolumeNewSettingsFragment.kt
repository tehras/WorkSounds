package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addToBundle
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.PresenterFragment
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule

/**
 * A placeholder fragment containing a simple view.
 */
class VolumeNewSettingsFragment : PresenterFragment<VolumeNewSettingsView, VolumeNewSettingsPresenter>(), VolumeNewSettingsView {
    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeFragmentModule(this)).injectTo(this)
    }

    companion object {
        val ARG_GROUP = "argument_group_settings"

        fun instance(group: VolumeSettingGroup?): VolumeNewSettingsFragment {
            return VolumeNewSettingsFragment().addToBundle {
                group?.let { putSerializable(ARG_GROUP, it) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_volume, container, false)
    }

    override fun onPresenterReady() {
        super.onPresenterReady()

        //start populating the layout
        //TODO if this is edit then we have slightly different behavior
        //horizontal chooser
        presenter.setUpHorizontalImagePicker()
    }

}
