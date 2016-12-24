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
                                var addFunc: () -> Unit,
                                var editFunc: (group: VolumeSettingGroup) -> Unit,
                                var deleteFunc: (group: VolumeSettingGroup) -> Unit) : RecyclerView.Adapter<AbstractViewHolder<VolumeSettingGroup>>() {

    private val VIEW_TYPE_DEFAULT_HOLD: Int = 0
    private val VIEW_TYPE_EMPTY: Int = 1

    fun update(volumeSettings: ArrayList<VolumeSettingGroup>?, addFunc: () -> Unit, editFunc: (group: VolumeSettingGroup) -> Unit, deleteFunc: (VolumeSettingGroup) -> Unit) {
        Timber.d("obtainAdapter")

        this.addFunc = addFunc
        this.volumeSettings = volumeSettings
        this.editFunc = editFunc
        this.deleteFunc = deleteFunc
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<VolumeSettingGroup>?, position: Int) {
        holder?.bindView(if (volumeSettings?.size ?: 0 > position) volumeSettings?.get(position) else null)
    }

    override fun getItemCount(): Int {
        return (volumeSettings?.size ?: 0) + 1// The one is for the "Add message"
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) != itemCount) VIEW_TYPE_DEFAULT_HOLD else VIEW_TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder<VolumeSettingGroup> {
        when (viewType) {
            VIEW_TYPE_EMPTY -> return VolumeSettingsEmptyViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_empty), addFunc)
            else -> return VolumeSettingsViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_item), editFunc, deleteFunc)
        }
    }

}