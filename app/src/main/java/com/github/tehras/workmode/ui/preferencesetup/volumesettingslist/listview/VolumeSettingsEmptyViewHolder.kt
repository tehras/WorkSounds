package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.view.View
import com.github.tehras.workmode.R
import com.github.tehras.workmode.ui.base.AbstractViewHolder
import timber.log.Timber

class VolumeSettingsEmptyViewHolder<in T>(view: View?, var addFunc: () -> Unit) : AbstractViewHolder<T>(view) {
    override fun bindView(t: T?) {
        Timber.d("Bind View")
        itemView.findViewById(R.id.clickable_layout).setOnClickListener {
            addFunc()
        }
    }
}