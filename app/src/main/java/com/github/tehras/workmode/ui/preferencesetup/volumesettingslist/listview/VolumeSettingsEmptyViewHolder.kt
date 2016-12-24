package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.view.View
import com.github.tehras.workmode.ui.base.AbstractViewHolder

class VolumeSettingsEmptyViewHolder<in T>(view: View?, var addFunc: () -> Unit) : AbstractViewHolder<T>(view) {
    override fun bindView(t: T?) {
        itemView.setOnClickListener { addFunc }
    }
}