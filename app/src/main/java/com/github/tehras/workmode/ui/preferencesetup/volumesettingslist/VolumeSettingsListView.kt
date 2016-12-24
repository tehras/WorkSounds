package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import com.github.tehras.workmode.models.settings.VolumeSettingGroup
import com.github.tehras.workmode.ui.base.MvpView

interface VolumeSettingsListView : MvpView {
    fun edit(group: VolumeSettingGroup)
    fun delete(group: VolumeSettingGroup)
    fun add()
}