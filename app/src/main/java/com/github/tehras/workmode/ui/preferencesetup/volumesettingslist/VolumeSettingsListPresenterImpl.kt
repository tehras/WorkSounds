package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview.VolumeSettingsListAdapter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class VolumeSettingsListPresenterImpl @Inject constructor(val preferences: SharedPreferences) : AbstractPresenter<VolumeSettingsListView>(), VolumeSettingsListPresenter {

    private var adapter: VolumeSettingsListAdapter? = null
    private val editFunc: (group: ScenePreference) -> Unit = { view?.edit(it) }
    private val deleteFunc: (group: ScenePreference) -> Unit = {
        ScenePreferenceSettings.deleteScene(it, preferences)
        view?.delete(it)
    }
    private val addFunc: () -> Unit = { view?.add() }

    /**
     * Returns List View Adapter
     */
    override fun obtainAdapter(): RecyclerView.Adapter<*>? {
        Timber.d("obtainAdapter")
        val setting = volumeSettings()

        if (adapter == null)
            adapter = VolumeSettingsListAdapter(setting, addFunc, editFunc, deleteFunc)
        else
            refreshAdapter()
        return adapter
    }

    override fun refreshAdapter() {
        adapter?.update(volumeSettings(), addFunc, editFunc, deleteFunc)
    }

    private fun volumeSettings(): ArrayList<ScenePreference>? {
        return ScenePreferenceSettings.getAllScenes(preference = preferences)
    }

}