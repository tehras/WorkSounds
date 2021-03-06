package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.app.Activity
import android.content.IntentFilter
import android.content.SharedPreferences
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.PreferencesLocationService
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.base.BaseActivity
import com.github.tehras.workmode.ui.base.BaseFragment
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview.VolumeSettingsListAdapter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class VolumeSettingsListPresenterImpl @Inject constructor(val preferences: SharedPreferences) : AbstractPresenter<VolumeSettingsListView>(), VolumeSettingsListPresenter {

    private var sendASuggestion: () -> Unit = {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo("koshkinbrosdev@gmail.com")
                .setSubject("Scenes - Suggestion")
                .setText("")
                .setChooserTitle("Email Client...")
                .startChooser()
    }

    private fun getActivity(): Activity? {
        if (view is Fragment) {
            return (view as Fragment).activity
        } else return null
    }

    private var adapter: VolumeSettingsListAdapter? = null
    private val editFunc: (group: ScenePreference, view: View) -> Unit = { group, v -> view?.edit(group, v) }
    private val deleteFunc: (group: ScenePreference) -> Unit = {
        delete()
        ScenePreferenceSettings.deleteScene(it, preferences)
        view?.delete(it)
    }

    private var helper: VolumeServiceInitHelper? = null

    override fun bindView(view: VolumeSettingsListView) {
        super.bindView(view)

        helper = VolumeServiceInitHelper(preferences, (view as Fragment).activity as BaseActivity, registerReceiver)
    }

    override fun unbindView() {
        super.unbindView()

        helper?.unbind()
    }

    /**
     * Returns List View Adapter
     */
    override fun obtainAdapter(): RecyclerView.Adapter<*>? {
        Timber.d("obtainAdapter")
        val setting = volumeSettings()

        if (adapter == null) {
            adapter = VolumeSettingsListAdapter(setting, editFunc, deleteFunc, sendASuggestion)
            startServices()
        } else
            refreshAdapter()
        return adapter
    }

    val registerReceiver: (PreferencesLocationService, IntentFilter) -> Unit = {
        prefService, intentFilter ->

        Timber.d("Trying to Register the Receiver - $view")
        view?.let {
            val register: (BaseActivity) -> Unit = { activity ->
                Timber.d("Registering the receiver in the Presenter")
                activity.application.registerReceiver(prefService, intentFilter)
            }
            if (it is BaseActivity) {
                register(it)
            } else if (it is BaseFragment && it.activity is BaseActivity) {
                register(it.activity as BaseActivity)
            }
        }
    }

    override fun refreshAdapter() {
        Timber.d("refreshing adapter ${volumeSettings()}")
        adapter?.update(volumeSettings(), editFunc, deleteFunc)

        startServices()
    }

    private fun startServices() {
        helper?.initialize()
        helper?.unregisterFence()
        helper?.registerFence()
    }

    private fun delete() {
        helper?.initialize()
        helper?.unregisterFence()
        helper?.registerFence()

    }

    override fun initFab(new_scene: FloatingActionButton?) {
        //check for size
        if (volumeSettings()?.size ?: 0 <= 4) {
            new_scene?.visibility = View.VISIBLE
            new_scene?.setOnClickListener { view?.add() }
        } else {
            new_scene?.visibility = View.GONE
        }
    }

    private fun volumeSettings(): ArrayList<ScenePreference>? {
        return ScenePreferenceSettings.getAllScenes(preference = preferences)
    }

}