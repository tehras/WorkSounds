package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.support.v7.widget.RecyclerView
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview.VolumeSettingsListAdapter
import java.util.*
import javax.inject.Inject

class VolumeSettingsListPresenterImpl @Inject constructor() : AbstractPresenter<VolumeSettingsListView>(), VolumeSettingsListPresenter {

    private var adapter: VolumeSettingsListAdapter? = null

    /**
     * Returns List View Adapter
     */
    override fun obtainAdapter(): RecyclerView.Adapter<*>? {
        val setting = volumeSettings()

        if (adapter == null)
            adapter = VolumeSettingsListAdapter(setting)
        else
            adapter?.update(setting)
        return adapter
    }

    private fun volumeSettings(): ArrayList<VolumeSettingGroup>? {
        return null //return
    }

}