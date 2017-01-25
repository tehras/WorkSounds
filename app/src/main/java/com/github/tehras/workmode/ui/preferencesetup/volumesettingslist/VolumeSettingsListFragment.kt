package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.defaultInit
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.PresenterFragment
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule
import kotlinx.android.synthetic.main.fragment_settings_list.*

open class VolumeSettingsListFragment : PresenterFragment<VolumeSettingsListView, VolumeSettingsListPresenter>(), VolumeSettingsListView {
    override fun add() {
        //start New Volume Settings Fragment
        if (activity is VolumeActivity)
            (activity as VolumeActivity).showNewVolumeFragment(null)
    }

    override fun edit(group: ScenePreference) {
        if (activity is VolumeActivity)
            (activity as VolumeActivity).showNewVolumeFragment(group)
    }

    override fun delete(group: ScenePreference) {
        view?.let {
            Snackbar.make(it, "Scene ${group.name} was removed from the list", Snackbar.LENGTH_SHORT).show()
        }
        presenter.refreshAdapter()
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeFragmentModule(this)).injectTo(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_list, container, false)
    }

    override fun onPresenterReady() {
        super.onPresenterReady()

        //start init
        fragment_setting_list_list_view.defaultInit()
        fragment_setting_list_list_view.adapter = presenter.obtainAdapter()

        presenter.initFab(new_scene)
    }

    fun refreshListView() {
        presenter.refreshAdapter()
    }
}