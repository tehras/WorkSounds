package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addBottomPadding
import com.github.tehras.workmode.extensions.defaultInit
import com.github.tehras.workmode.extensions.rotateFullCircle
import com.github.tehras.workmode.extensions.setTextColor
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.PresenterFragment
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule
import kotlinx.android.synthetic.main.fragment_settings_list.*
import timber.log.Timber


open class VolumeSettingsListFragment : PresenterFragment<VolumeSettingsListView, VolumeSettingsListPresenter>(), VolumeSettingsListView {
    override fun add() {
        Timber.d("add called")
        //start New Volume Settings Fragment
        if (activity is VolumeActivity)
            (activity as VolumeActivity).showNewVolumeFragment(null, new_scene)
    }

    override fun edit(group: ScenePreference, animateView: View) {
        Timber.d("edit called")
        if (activity is VolumeActivity)
            (activity as VolumeActivity).showNewVolumeFragment(group, animateView)
    }

    override fun delete(group: ScenePreference) {
        view?.let {
            Snackbar.make(it, "${group.name} was removed from the list", Snackbar.LENGTH_SHORT)
                    .setTextColor(Color.WHITE).show()
        }
        presenter.refreshAdapter()
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeFragmentModule(this)).injectTo(this)
    }

    override fun onStart() {
        super.onStart()

        Timber.i("onStart")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_list, container, false)
    }

    override fun onPresenterReady() {
        super.onPresenterReady()

        Timber.i("onPresenterReady - $presenter")

        //start init
        fragment_setting_list_list_view.defaultInit()
        if (firstLoad)
            fragment_setting_list_list_view.addBottomPadding(R.dimen.extra_bottom_padding)
        fragment_setting_list_list_view.adapter = presenter.obtainAdapter()

        presenter.initFab(new_scene)

        volume_settings_icon.setOnClickListener {
            it.rotateFullCircle { launchSettingsScreen() }
        }
    }

    private fun launchSettingsScreen() {
        if (activity is VolumeActivity)
            (activity as VolumeActivity).launchSettingsScreen()
    }

    fun refreshListView() {
        presenter.refreshAdapter()
    }
}