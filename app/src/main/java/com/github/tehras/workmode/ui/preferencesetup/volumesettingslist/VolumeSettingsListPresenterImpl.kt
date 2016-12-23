package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.support.v7.widget.RecyclerView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.AudioSettings
import com.github.tehras.workmode.models.VolumeLocation
import com.github.tehras.workmode.models.settings.VolumeLocations
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview.VolumeSettingsListAdapter
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class VolumeSettingsListPresenterImpl @Inject constructor() : AbstractPresenter<VolumeSettingsListView>(), VolumeSettingsListPresenter {

    private var adapter: VolumeSettingsListAdapter? = null

    /**
     * Returns List View Adapter
     */
    override fun obtainAdapter(): RecyclerView.Adapter<*>? {
        Timber.d("obtainAdapter")
        val setting = volumeSettings()

        if (adapter == null)
            adapter = VolumeSettingsListAdapter(setting, { view?.edit(it) }, {view?.delete(it)})
        else
            adapter?.update(setting)
        return adapter
    }

    private fun volumeSettings(): ArrayList<VolumeSettingGroup>? {
        return arrayListOf(VolumeSettingGroup("Test", AudioSettings(10, 2), AudioSettings(10, 1), VolumeLocations(arrayListOf(VolumeLocation(LatLng(0.00, 0.00)))), R.drawable.ic_work_tile_black))//return
    }

}