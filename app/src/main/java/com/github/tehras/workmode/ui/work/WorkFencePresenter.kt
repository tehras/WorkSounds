package com.github.tehras.workmode.ui.work

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import com.github.tehras.workmode.services.WorkLocationService
import com.github.tehras.workmode.shared.LocationPreferenceSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.base.BaseActivity
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.google.android.gms.awareness.fence.LocationFence
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallbacks
import com.google.android.gms.common.api.Status
import timber.log.Timber


open class WorkFencePresenter constructor(private var preferences: SharedPreferences) : AbstractPresenter<WorkView>() {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFencePendingIntent: PendingIntent? = null
    private var fenceReceiver: WorkLocationService? = null

    fun initFenceApi() {
        val location = LocationPreferenceSettings.getLocation(preferences)
        if (location != null) {
            LocationPreferenceSettings.getLocation(preferences) != null
            //initialize
            mGoogleApiClient = GoogleApiClient.Builder(view as BaseActivity)
                    .addApi(Awareness.API)
                    .build()
            mGoogleApiClient?.connect()

            fenceReceiver = WorkLocationService()
            val intent = Intent(FENCE_RECEIVER_ACTION)
            mFencePendingIntent = PendingIntent.getBroadcast(view as BaseActivity,
                    FENCE_RECEIVER_CODE,
                    intent,
                    0)

            registerFence()
            (view as BaseActivity).registerReceiver(fenceReceiver, IntentFilter(FENCE_RECEIVER_ACTION))
        }
    }

    private fun registerFence() {
        val location = LocationPreferenceSettings.getLocation(preferences)

        val locationFence = LocationFence.`in`(location?.location?.latitude ?: 0.00, location?.location?.longitude ?: 0.00, DEFAULT_RADIUS, DWELL_TIME_MILLIS)
        val locationEnteringFence = LocationFence.entering(location?.location?.latitude ?: 0.00, location?.location?.longitude ?: 0.00, DEFAULT_RADIUS)


        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                FenceUpdateRequest.Builder()
                        .addFence(FENCE_RECEIVER_ACTION_KEY, locationFence, mFencePendingIntent)
                        .addFence(FENCE_RECEIVER_ACTION_KEY, locationEnteringFence, mFencePendingIntent)
                        .build())
                .setResultCallback { status ->
                    if (status.isSuccess) {
                        Timber.d("Fence was successfully registered.")
                    } else {
                        Timber.d("Fence could not be registered: " + status)
                    }
                }
    }


    private fun unregisterFence() {
        mGoogleApiClient?.let {
            Awareness.FenceApi.updateFences(
                    mGoogleApiClient,
                    FenceUpdateRequest.Builder()
                            .removeFence(FENCE_RECEIVER_ACTION_KEY)
                            .build())
                    .setResultCallback(object : ResultCallbacks<Status>() {
                        override fun onSuccess(status: Status) {
                            Timber.d("Fence $FENCE_RECEIVER_ACTION_KEY successfully removed.")
                        }

                        override fun onFailure(status: Status) {
                            Timber.d("Fence $FENCE_RECEIVER_ACTION_KEY could NOT be removed.")
                        }
                    })
        }
    }

    override fun unbindView() {
        unregisterFence()
        fenceReceiver?.let {
            (view as BaseActivity).unregisterReceiver(fenceReceiver)
        }

        super.unbindView()
    }

    companion object {
        val DEFAULT_RADIUS = 200.toDouble() //this is in meters
        val DWELL_TIME_MILLIS = 30000.toLong() //30 seconds

        val FENCE_RECEIVER_CODE = 10001
        val FENCE_RECEIVER_ACTION = "FENCE_RECEIVE"
        val FENCE_RECEIVER_ACTION_KEY = "fence_receiver_key"
    }

}