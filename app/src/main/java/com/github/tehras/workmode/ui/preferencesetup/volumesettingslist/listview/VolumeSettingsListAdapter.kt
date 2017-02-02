package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.inflateLayoutFromParent
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import java.util.*

class VolumeSettingsListAdapter(var volumeSettings: ArrayList<ScenePreference>?,
                                var editFunc: (group: ScenePreference) -> Unit,
                                var deleteFunc: (group: ScenePreference) -> Unit) : RecyclerView.Adapter<AbstractViewHolder<ScenePreference>>() {

    private val VIEW_TYPE_DEFAULT_HOLD: Int = 0
    private val VIEW_TYPE_EMPTY: Int = 1

    fun update(volumeSettings: ArrayList<ScenePreference>?, editFunc: (ScenePreference) -> Unit, deleteFunc: (ScenePreference) -> Unit) {
        this.volumeSettings = volumeSettings
        this.editFunc = editFunc
        this.deleteFunc = deleteFunc

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<ScenePreference>?, position: Int) {
        holder?.bindView(if (volumeSettings?.size ?: 0 > position) volumeSettings?.get(position) else null)
    }

    override fun getItemCount(): Int {
        val size = getSize()

        if (size == 0)
            return 1

        return size
    }

    private fun getSize(): Int {
        return volumeSettings?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val size = getSize()

        if (size == 0) return VIEW_TYPE_EMPTY else return VIEW_TYPE_DEFAULT_HOLD
//        return if ((position + 1) != itemCount) VIEW_TYPE_DEFAULT_HOLD else VIEW_TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<ScenePreference> {
        when (viewType) {
            VIEW_TYPE_EMPTY -> return VolumeSettingsEmptyViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_empty_text))
            else -> return VolumeSettingsViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_item), editFunc, deleteFunc, { notifyDataSetChanged() })
        }
    }

}