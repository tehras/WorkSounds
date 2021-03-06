package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.ServiceHelper
import com.github.tehras.workmode.services.ServiceHelper.soundUpdated
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import com.github.tehras.workmode.views.VolumeProgressLayout

@Suppress("DEPRECATION")
class VolumeSettingsViewHolder(var view: View?, var editFunc: (group: ScenePreference, view: View) -> Unit, var deleteFunc: (group: ScenePreference) -> Unit, var volumeAdjusted: () -> Unit) : AbstractViewHolder<ScenePreference>(view) {
    @SuppressLint("SetTextI18n")
    override fun bindView(t: ScenePreference?, position: Int) {
        (view?.findViewById(R.id.volume_name) as TextView).text = ("${t?.name} (Scene ${position + 1})")
        (view?.findViewById(R.id.volume_image) as ImageView).setImageResource(t?.selectedTile?.blackTile ?: R.drawable.ic_work_tile)
        (view?.findViewById(R.id.volume_image) as ImageView).setColorFilter(view?.resources?.getColor(R.color.colorPrimary) ?: Color.BLACK, PorterDuff.Mode.SRC_ATOP)

        //check if current volume is the same
        t?.let {
            (view?.findViewById(R.id.ring_progress_bar) as View).visibility = View.GONE
            (view?.findViewById(R.id.media_progress_bar) as View).visibility = View.GONE

            it.inRingVolume?.let {
                (view?.findViewById(R.id.ring_progress_bar) as View).visibility = View.VISIBLE
                (view?.findViewById(R.id.ring_progress_bar) as VolumeProgressLayout).setVolumeLevel(it.setMusicVolume, it.maxMusicVolume)
            }
            it.inMediaVolume?.let {
                (view?.findViewById(R.id.media_progress_bar) as View).visibility = View.VISIBLE
                (view?.findViewById(R.id.media_progress_bar) as VolumeProgressLayout).setVolumeLevel(it.setMusicVolume, it.maxMusicVolume)
            }


            val isEnabled = ServiceHelper.isCurrentlyEnabled(view?.context, t)
            val text: String
            val clickListener: () -> Unit
            if (isEnabled) {
                clickListener = {
                    view?.context?.let { context ->
                        ServiceHelper.disableScene(scene = it, context = context) {
                            volumeChanged(context)
                        }
                    }

                }
                text = "Disable"
            } else {
                clickListener = {
                    view?.context?.let { context ->
                        ServiceHelper.enableScene(it, view?.context, getPreferences(context)) {
                            volumeChanged(context)
                        }
                    }
                }
                text = "Enable"
            }

            (view?.findViewById(R.id.volume_enable_disable) as TextView).text = text
            (view?.findViewById(R.id.volume_enable_disable) as TextView).setOnClickListener { clickListener() }

            (view?.findViewById(R.id.volume_edit) as View).setOnClickListener { editFunc(t, view!!) }
            (view?.findViewById(R.id.volume_delete) as View).setOnClickListener { deleteFunc(t) }

            if (it.wifiEnabled) {
                (view?.findViewById(R.id.wifi_list_item_text) as TextView).text = "Wi-Fi will be ${if (it.wifiState) "enabled" else "disabled"}"
                if (it.wifiState)
                    (view?.findViewById(R.id.wifi_list_item_text) as TextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_primary, 0, 0, 0)
                else
                    (view?.findViewById(R.id.wifi_list_item_text) as TextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_off_primary, 0, 0, 0)
                (view?.findViewById(R.id.wifi_list_item_text) as TextView).visibility = View.VISIBLE
            } else {
                (view?.findViewById(R.id.wifi_list_item_text) as TextView).visibility = View.GONE
            }
        }
    }

    private fun volumeChanged(context: Context) {
        context.soundUpdated()
        //notify the adapter items may have changed
        volumeAdjusted()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

}