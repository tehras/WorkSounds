package com.github.tehras.workmode.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_ENTRY_KEY
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_KEY
import com.google.android.gms.awareness.fence.FenceState
import timber.log.Timber

class PreferencesLocationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val fenceState = FenceState.extract(intent)

        Timber.d("Fence Receiver Received")

        if (fenceState.fenceKey.startsWith(FENCE_RECEIVER_ACTION_KEY, true)) {
            //todo get the fence
            Timber.d("Fence entered -> ${fenceState.fenceKey}")
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    Timber.d("Fence > Currently at work.")
                }
                FenceState.FALSE -> {
                    Timber.d("Fence > Currently NOT at work.")
                }
                FenceState.UNKNOWN -> {
                    Timber.d("Fence > Current Location UNKNOWN.")
                }
            }
        } else if (fenceState.fenceKey.startsWith(FENCE_RECEIVER_ACTION_ENTRY_KEY, true)) {
            Timber.d("Fence entered -> ${fenceState.fenceKey}")
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    Timber.d("Fence > Currently at work.")
                }
                FenceState.FALSE -> {
                    Timber.d("Fence > Currently NOT at work.")
                }
                FenceState.UNKNOWN -> {
                    Timber.d("Fence > Current Location UNKNOWN.")
                }
            }
        }
    }
}