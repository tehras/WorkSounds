package com.github.tehras.workmode.ui.preferencesetup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.enterCircularReveal
import com.github.tehras.workmode.extensions.getLastFragmentInStack
import com.github.tehras.workmode.extensions.startFragment
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.PresenterActivity
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsFragment
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListFragment
import com.github.tehras.workmode.ui.work.WorkPresenterImpl
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_volume.*
import timber.log.Timber

class VolumeActivity : PresenterActivity<VolumeView, VolumePresenter>(), VolumeView {
    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeModule(this)).injectTo(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_volume)

        if (savedInstanceState == null) {
            //start first fragment
            this.enterCircularReveal(root_view) //animate
        }
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

    fun refreshScene() {
        supportFragmentManager.fragments.forEach {
            if (it is VolumeSettingsListFragment)
                it.refreshListView()
        }
    }

    var saveLocation: ((place: Place) -> Unit)? = null

    fun startForLocation(saveLocation: ((place: Place) -> Unit)) {
        this.saveLocation = saveLocation

        try {
            this.startActivityForResult(Intent(PlacePicker.IntentBuilder().build(this)), WorkPresenterImpl.PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            AlertDialog.Builder(this).setMessage("Google Play Services are required. Please download/update and try again")
                    .setPositiveButton("Download", { d, i ->
                        d.dismiss()
                        val appPackageName = "com.google.android.gms"
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                        } catch (anfe: android.content.ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
                        }

                    })
                    .setNegativeButton("Cancel", { d, i ->
                        d.dismiss()
                    }).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult $requestCode, $requestCode, $data")
        if (requestCode === WorkPresenterImpl.PLACE_PICKER_REQUEST) {
            if (resultCode === Activity.RESULT_OK || resultCode === Activity.RESULT_FIRST_USER) {
                val selectedPlace = PlacePicker.getPlace(this, data)
                Timber.d("selectedPlace - $selectedPlace")

                //save the place
                saveLocation?.let {
                    it(selectedPlace)
                }
            }
        }
    }

}
