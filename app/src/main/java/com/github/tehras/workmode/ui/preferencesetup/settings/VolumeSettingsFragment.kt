package com.github.tehras.workmode.ui.preferencesetup.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.*
import com.github.tehras.workmode.ui.base.PresenterFragment
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule
import kotlinx.android.synthetic.main.fragment_settings_view.*

class VolumeSettingsFragment : PresenterFragment<VolumeSettingsView, VolumeSettingsPresenter>(), VolumeSettingsView {

    companion object {
        val CX = "arg_center_x"
        val CY = "arg_center_y"
        val RADIUS = "arg_radius"

        fun instance(view: View): VolumeSettingsFragment {
            return VolumeSettingsFragment().addToBundle {
                putInt(CX, view.centerX())
                putInt(CY, view.centerY())
                putInt(RADIUS, view.radius())
            }
        }
    }

    override fun close() {
        activity.onBackPressed()
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeFragmentModule(this)).injectTo(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_view, container, false)
    }

    override fun onPresenterReady() {
        super.onPresenterReady()

        presenter.setUpNotificationSettings(notifications_switch)
        presenter.setUpLocationSettings(location_edit_text)
        presenter.setUpUpdateButton(update_button)
        presenter.setUpCancelButton(cancel_button)

        settings_overall_layout.circularReveal(arguments.getInt(CX), arguments.getInt(CY), arguments.getInt(RADIUS))
    }

}