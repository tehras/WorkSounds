package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import com.github.tehras.workmode.ui.base.Presenter

interface VolumeSettingsListPresenter : Presenter<VolumeSettingsListView> {
    fun obtainAdapter(): RecyclerView.Adapter<*>?
    fun refreshAdapter()
    fun initFab(new_scene: FloatingActionButton?)
}