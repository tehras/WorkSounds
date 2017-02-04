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
                                var deleteFunc: (group: ScenePreference) -> Unit,
                                var sendASuggestion: () -> Unit) : RecyclerView.Adapter<AbstractViewHolder<ScenePreference>>() {

    private val VIEW_TYPE_DEFAULT_HOLD: Int = 0
    private val VIEW_TYPE_EMPTY: Int = 1
    private val VIEW_TYPE_SUGGEST: Int = 2

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
        val size = getSize() + 1

        if (size == 0)
            return 1

        return size
    }

    private fun getSize(): Int {
        return volumeSettings?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val size = getSize()

        if (size == 0) return VIEW_TYPE_EMPTY
        else if (position == itemCount - 1) {
            return VIEW_TYPE_SUGGEST
        } else {
            return VIEW_TYPE_DEFAULT_HOLD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<ScenePreference> {
        when (viewType) {
            VIEW_TYPE_SUGGEST -> return VolumeSettingsSuggestion(parent.inflateLayoutFromParent(R.layout.volume_list_make_suggestion), sendASuggestion = sendASuggestion)
            VIEW_TYPE_EMPTY -> return VolumeSettingsEmptyViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_empty_text))
            else -> return VolumeSettingsViewHolder(parent.inflateLayoutFromParent(R.layout.volume_list_view_item), editFunc, deleteFunc, { notifyDataSetChanged() })
        }
    }

}