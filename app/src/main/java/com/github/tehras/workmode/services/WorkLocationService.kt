package com.github.tehras.workmode.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.github.tehras.workmode.ui.work.WorkFencePresenter.Companion.FENCE_RECEIVER_ACTION_KEY
import com.google.android.gms.awareness.fence.FenceState
import timber.log.Timber

class WorkLocationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val fenceState = FenceState.extract(intent)

        Timber.d("Fence Receiver Received")

        if (TextUtils.equals(fenceState.fenceKey, FENCE_RECEIVER_ACTION_KEY)) {
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
