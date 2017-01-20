package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addToBundle
import com.github.tehras.workmode.extensions.getColorDefault
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.PresenterFragment
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule
import kotlinx.android.synthetic.main.fragment_volume.*
import timber.log.Timber

/**
 * A placeholder fragment containing a simple view.
 */
class VolumeNewSettingsFragment : PresenterFragment<VolumeNewSettingsView, VolumeNewSettingsPresenter>(), VolumeNewSettingsView {

    var showedAlertMessage: Boolean = false

    override fun showCancelDialog() {
        showedAlertMessage = true
        AlertDialog.Builder(this.context)
                .setMessage("Are you sure you want to cancel this scene, all progress will be lost?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    goBackWithoutSaving()
                }
                .setNegativeButton("No") { dialog, i ->
                    dialog.dismiss()
                    showedAlertMessage = false
                }
                .show()
    }

    fun alreadyShowedCancelMessage(): Boolean {
        Timber.d("already showed cancel message -> $showedAlertMessage")
        return showedAlertMessage
    }

    override fun goBackWithoutSaving() {
        if (activity is VolumeActivity) {
            (activity as VolumeActivity).onBackPressedIgnoreOverride()
        }
        activity.onBackPressed()
    }

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
        //horizontal chooser
        presenter.setUpHorizontalImagePicker(image_selector_list_view)
        presenter.setUpInVolumeControls(linearLayout)
        presenter.setUpOutVolumeControls(out_ring_volume_container)
        presenter.setUpButtonBar(cancel_button, create_button)
        presenter.setUpName(name_field)
    }

    override fun showTileNeedsToBeSelected(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "Please select a tile", Snackbar.LENGTH_SHORT).show()

                icon_title.setTextColor(it.context.getColorDefault(R.color.errorRed))
            } else {
                icon_title.setTextColor(it.context.getColorDefault(android.R.color.black))
            }
        }
    }

    override fun showNameNeedsToBeSelected(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "Please enter a name", Snackbar.LENGTH_SHORT).show()

                name_title.setTextColor(it.context.getColorDefault(R.color.errorRed))
            } else {
                name_title.setTextColor(it.context.getColorDefault(android.R.color.black))
            }
        }
    }

    override fun notifySceneSubmitted() {
        create_button.visibility = View.INVISIBLE
        success_layout.show {
            //close fragment
            if (activity is VolumeActivity) {
                showedAlertMessage = true
                activity.onBackPressed()
                (activity as VolumeActivity).refreshScene()
            }
        }
    }

}
