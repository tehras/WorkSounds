package com.github.tehras.workmode.ui.work

import com.github.tehras.workmode.ui.base.BaseActivity
import com.github.tehras.workmode.ui.base.Presenter

interface WorkPresenter : Presenter<WorkView> {
    fun retrieveWorkTileSettings()
    fun retrieveSoundSettings()
    fun enableWorkTile(enable: Boolean)
    fun isRingSettingDifferent(current: Int, max: Int): Boolean
    fun isMusicSettingDifferent(current: Int, max: Int): Boolean
    fun updateRingSettings(current: Int, max: Int)
    fun updateMusicSettings(current: Int, max: Int)
    fun checkForDnDOptions(activity: BaseActivity)
}