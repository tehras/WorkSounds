package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.inflateLayoutFromParent
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import timber.log.Timber
import java.util.*

class VolumeSettingsListAdapter(var volumeSettings: ArrayList<VolumeSettingGroup>?,
                                var editFunc: (group: VolumeSettingGroup) -> Unit,
                                var deleteFunc: (group: VolumeSettingGroup) -> Unit) : RecyclerView.Adapter<AbstractViewHolder<VolumeSettingGroup>>() {

    private val VIEW_TYPE_DEFAULT_HOLD: Int = 0
    private val VIEW_TYPE_EMPTY_ERROR: Int = 1

    fun update(volumeSettings: ArrayList<VolumeSettingGroup>?) {
        this.volumeSettings = volumeSettings
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<VolumeSettingGroup>?, position: Int) {
        holder?.bindView(volumeSettings?.get(position))
    }

    override fun getItemCount(): Int {
        Timber.d("itemCount - ${volumeSettings?.size}")
        return volumeSettings?.size ?: 1 // The one is for the "Add message"
    }

    override fun getItemViewType(position: Int): Int {
        return if (volumeSettings?.size ?: 0 > 0) VIEW_TYPE_DEFAULT_HOLD else VIEW_TYPE_EMPTY_ERROR
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder<VolumeSettingGroup> {
        when (viewType) {
            VIEW_TYPE_EMPTY_ERROR -> return VolumeSettingsEmptyViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_empty))
            else -> return VolumeSettingsViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_item), editFunc, deleteFunc)
        }
    }

}