package com.github.tehras.workmode.ui.work

import com.github.tehras.workmode.ui.base.MvpView

interface WorkView : MvpView {
    fun hideTileCard()
    fun workTileStatus(isEnabled: Boolean)
    fun updateMusicControls(currMusicVolume: Int, maxMusicVolume: Int)
    fun updateRingControls(currMusicVolume: Int, maxMusicVolume: Int)
}