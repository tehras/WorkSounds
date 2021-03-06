package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.*
import com.github.tehras.workmode.models.scene.ScenePreference
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
    var scene: ScenePreference? = null
    var centerX: Int? = null
    var centerY: Int? = null
    var radius: Int? = null

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
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeFragmentModule(this)).injectTo(this)
    }

    companion object {
        val ARG_GROUP = "argument_group_settings"
        val ARG_CENTER_X = "argument_center_x"
        val ARG_CENTER_Y = "argument_center_y"
        val ARG_RADIUS = "argument_radius"

        fun instance(group: ScenePreference?, animateFrom: View): VolumeNewSettingsFragment {
            return VolumeNewSettingsFragment().addToBundle {
                group?.let {
                    putSerializable(ARG_GROUP, it)
                }

                putInt(ARG_RADIUS, animateFrom.radius())
                putInt(ARG_CENTER_X, animateFrom.centerX())
                putInt(ARG_CENTER_Y, animateFrom.centerY())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_volume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.let {
            restoreArgs(it)
        }
        arguments?.let {
            restoreArgs(it)
        }
    }

    private fun restoreArgs(it: Bundle): Unit {
        scene = it.getSerializable(ARG_GROUP) as ScenePreference?
        centerX = it.getInt(ARG_CENTER_X)
        centerY = it.getInt(ARG_CENTER_Y)
        radius = it.getInt(ARG_RADIUS)
    }

    override fun onPresenterReady() {
        super.onPresenterReady()

        //start populating the layout
        scene?.let {
            presenter.setEditLayout(it)
        }

        //horizontal chooser
        presenter.setUpHorizontalImagePicker(image_selector_list_view)
        presenter.setUpVolumeControls(view)
        presenter.setUpWiFiControls(view)
        presenter.setUpButtonBar(cancel_button, create_button)
        presenter.setUpName(name_field)
        presenter.setUpLocation(location_layout)

        volume_scroll_view.viewTreeObserver.addOnScrollChangedListener(VolumeNewListener(volume_scroll_view, top_divider_title, mandatory_fields_divider, non_mandatory_fields))

        //animate
        overall_layout.circularReveal(centerX ?: 0, centerY ?: 0, radius ?: 0)
    }

    override fun showTileNeedsToBeSelected(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "Please select a tile", Snackbar.LENGTH_SHORT).setTextColor(Color.WHITE).show()

                icon_title.setTextColor(it.context.getColorDefault(R.color.errorRed))
            } else {
                icon_title.setTextColor(it.context.getColorDefault(android.R.color.black))
            }
        }
    }

    override fun showNameNeedsToBeSelected(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "Please enter a name", Snackbar.LENGTH_SHORT).setTextColor(Color.WHITE).show()

                name_title.setTextColor(it.context.getColorDefault(R.color.errorRed))
            } else {
                name_title.setTextColor(it.context.getColorDefault(android.R.color.black))
            }
        }
    }

    override fun showLocationNeedsToBeSelected(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "Please select a location", Snackbar.LENGTH_SHORT).setTextColor(Color.WHITE).show()

                location_button_label.setTextColor(it.context.getColorDefault(R.color.errorRed))
            } else {
                name_title.setTextColor(it.context.getColorDefault(android.R.color.black))
            }
        }
    }

    override fun showAtLeastOneNonMandatoryField(b: Boolean) {
        view?.let {
            if (b) {
                Snackbar.make(it, "At least 1 preference must be enabled", Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).show()

                non_mandatory_fields.setTextColor(R.color.errorRed)
            } else {
                non_mandatory_fields.setTextColor(android.R.color.white)
            }
        }
    }

    override fun notifySceneSubmitted() {
        create_button.visibility = View.INVISIBLE
        success_layout.show {
            //close fragment
            if (activity is VolumeActivity) {
                showedAlertMessage = true
                activity?.let {
                    (activity as VolumeActivity).refreshScene()
                }

                activity.onBackPressed()
            }
        }
    }

    val locationListener: View.OnClickListener = View.OnClickListener {
        if (activity is VolumeActivity) {
            (activity as VolumeActivity).startForLocation {
                presenter.saveLocation(it)
                showLocationNeedsToBeSelected(false)
            }
        }
    }

    override fun showLocationExisted(address: String?) {
        location_selected_view.visibility = View.VISIBLE
        select_location_view.visibility = View.GONE

        current_location_value.text = address
        current_location_value.setOnClickListener(locationListener)

    }

    override fun showSelectLocation() {
        location_selected_view.visibility = View.GONE
        select_location_view.visibility = View.VISIBLE

        location_button.setButtonColor(android.R.color.white)
        location_button.setOnClickListener(locationListener)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        scene?.let {
            outState?.putSerializable(ARG_GROUP, scene)
        }
    }

}
