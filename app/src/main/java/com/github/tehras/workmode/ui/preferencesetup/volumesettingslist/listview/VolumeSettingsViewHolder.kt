package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import com.github.tehras.workmode.views.VolumeProgressLayout

@Suppress("DEPRECATION")
class VolumeSettingsViewHolder(var view: View?, var editFunc: (group: ScenePreference) -> Unit, var deleteFunc: (group: ScenePreference) -> Unit) : AbstractViewHolder<ScenePreference>(view) {
    override fun bindView(t: ScenePreference?) {
        (view?.findViewById(R.id.volume_name) as TextView).text = t?.name ?: ""
        (view?.findViewById(R.id.ring_progress_bar) as VolumeProgressLayout).setVolumeLevel(t?.inRingVolume?.setMusicVolume ?: 0, t?.inRingVolume?.maxMusicVolume ?: 0)
        (view?.findViewById(R.id.media_progress_bar) as VolumeProgressLayout).setVolumeLevel(t?.inMediaVolume?.setMusicVolume ?: 0, t?.inMediaVolume?.maxMusicVolume ?: 0)
        (view?.findViewById(R.id.volume_image) as ImageView).setImageResource(t?.selectedTile?.blackTile ?: R.drawable.ic_work_tile)
        (view?.findViewById(R.id.volume_image) as ImageView).setColorFilter(view?.resources?.getColor(R.color.colorPrimary) ?: Color.BLACK, PorterDuff.Mode.SRC_ATOP)

        t?.let {
            (view?.findViewById(R.id.volume_edit) as View).setOnClickListener { editFunc(t) }
            (view?.findViewById(R.id.volume_delete) as View).setOnClickListener { deleteFunc(t) }
        }
    }

}