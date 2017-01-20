package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import com.github.tehras.workmode.ui.base.MvpView

interface VolumeNewSettingsView : MvpView {
    fun goBackWithoutSaving()
    fun showCancelDialog()
    fun showTileNeedsToBeSelected(b: Boolean)
    fun showNameNeedsToBeSelected(b: Boolean)
    fun notifySceneSubmitted()
}