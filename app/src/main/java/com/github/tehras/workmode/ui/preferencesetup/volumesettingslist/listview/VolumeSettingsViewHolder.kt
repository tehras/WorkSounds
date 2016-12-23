package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import com.github.tehras.workmode.views.VolumeProgressLayout

@Suppress("DEPRECATION")
class VolumeSettingsViewHolder(var view: View?, var editFunc: (group: VolumeSettingGroup) -> Unit, var deleteFunc: (group: VolumeSettingGroup) -> Unit) : AbstractViewHolder<VolumeSettingGroup>(view) {
    override fun bindView(t: VolumeSettingGroup?) {
        (view?.findViewById(R.id.volume_name) as TextView).text = t?.name ?: ""
        (view?.findViewById(R.id.ring_progress_bar) as VolumeProgressLayout).setVolumeLevel(t?.ringSetting?.setMusicVolume ?: 0, t?.ringSetting?.maxMusicVolume ?: 0)
        (view?.findViewById(R.id.media_progress_bar) as VolumeProgressLayout).setVolumeLevel(t?.mediaSetting?.setMusicVolume ?: 0, t?.mediaSetting?.maxMusicVolume ?: 0)
        (view?.findViewById(R.id.volume_image) as ImageView).setImageResource(t?.image ?: R.drawable.ic_work_tile)
        (view?.findViewById(R.id.volume_image) as ImageView).setColorFilter(view?.resources?.getColor(R.color.colorPrimary) ?: Color.BLACK, PorterDuff.Mode.SRC_ATOP)

        t?.let {
            (view?.findViewById(R.id.volume_edit) as TextView).setOnClickListener { editFunc(t) }
            (view?.findViewById(R.id.volume_delete) as TextView).setOnClickListener { deleteFunc(t) }
        }
    }

}