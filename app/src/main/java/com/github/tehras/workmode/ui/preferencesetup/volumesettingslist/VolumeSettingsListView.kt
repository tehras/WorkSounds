package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.view.View
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.MvpView

interface VolumeSettingsListView : MvpView {
    fun edit(group: ScenePreference, animateView: View)
    fun delete(group: ScenePreference)
    fun add()
}