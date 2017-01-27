package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.PreferencesLocationService
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.base.BaseActivity
import com.github.tehras.workmode.ui.work.WorkFencePresenter.Companion.FENCE_RECEIVER_CODE
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallbacks
import com.google.android.gms.common.api.Status
import timber.log.Timber


class VolumeServiceInitHelper(val preferences: SharedPreferences, val activity: BaseActivity) {

    companion object {
        val DEFAULT_RADIUS = 200.toDouble() //this is in meters
        val DWELL_TIME_MILLIS = 30000.toLong() //30 seconds

        val FENCE_RECEIVER_ACTION = "FENCE_RECEIVE"
        val FENCE_RECEIVER_ACTION_KEY = "fence_receiver_key"
        val FENCE_RECEIVER_ACTION_ENTRY_KEY = "fence_receiver_entry_key"
        val FENCE_RECEIVER_ACTION_EXIT_KEY = "fence_receiver_exit_key"
    }

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFencePendingIntent: PendingIntent? = null
    private var fenceReceiver: PreferencesLocationService? = null

    init {
        initialize()
    }

    fun initialize() {
        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)
        if (preferences.isNotEmpty()) {
            //initialize
            mGoogleApiClient = GoogleApiClient.Builder(activity)
                    .addApi(Awareness.API)
                    .build()
            mGoogleApiClient?.connect()

            val intent = Intent(FENCE_RECEIVER_ACTION)
            mFencePendingIntent = PendingIntent.getBroadcast(activity,
                    FENCE_RECEIVER_CODE,
                    intent,
                    0)
        }
    }


    fun registerFence(registerReceiver: (PreferencesLocationService, IntentFilter) -> Unit) {
        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)

        if (preferences.isNotEmpty()) {
            preferences.forEach {
                val locationFence = LocationFence.`in`(it.location?.location?.latitude ?: 0.00, it.location?.location?.longitude ?: 0.00, DEFAULT_RADIUS, DWELL_TIME_MILLIS)
                val locationEnteringFence = LocationFence.entering(it.location?.location?.latitude ?: 0.00, it.location?.location?.longitude ?: 0.00, DEFAULT_RADIUS)
                val locationExitingFence = LocationFence.exiting(it.location?.location?.latitude ?: 0.00, it.location?.location?.longitude ?: 0.00, DEFAULT_RADIUS)

                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        FenceUpdateRequest.Builder()
                                .addFence(createKey(it), locationFence, mFencePendingIntent)
                                .addFence(createEntryKey(it), locationEnteringFence, mFencePendingIntent)
                                .addFence(createExitKey(it), locationExitingFence, mFencePendingIntent)
                                .build())
                        .setResultCallback { status ->
                            if (status.isSuccess) {
                                Timber.d("Fence was successfully registered.")
                            } else {
                                Timber.d("Fence could not be registered: " + status)
                            }
                        }
            }
        }
        fenceReceiver = PreferencesLocationService()
        registerReceiver(fenceReceiver!!, IntentFilter(FENCE_RECEIVER_ACTION))
    }

    private fun createKey(it: ScenePreference): String {
        return "${FENCE_RECEIVER_ACTION_KEY}_${it.id}"
    }

    private fun createEntryKey(it: ScenePreference): String {
        return "${FENCE_RECEIVER_ACTION_ENTRY_KEY}_${it.id}"
    }

    private fun createExitKey(it: ScenePreference): String {
        return "${FENCE_RECEIVER_ACTION_ENTRY_KEY}_${it.id}"
    }

    fun unregisterFence() {
        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)

        if (preferences.isNotEmpty()) {
            preferences.forEach { pref ->
                mGoogleApiClient?.let {
                    val key = createKey(pref)
                    val entryKey = createEntryKey(pref)
                    Awareness.FenceApi.updateFences(
                            mGoogleApiClient,
                            FenceUpdateRequest.Builder()
                                    .removeFence(key)
                                    .removeFence(entryKey)
                                    .build())
                            .setResultCallback(object : ResultCallbacks<Status>() {
                                override fun onSuccess(status: Status) {
                                    Timber.d("Fence $key and $entryKey successfully removed.")
                                }

                                override fun onFailure(status: Status) {
                                    Timber.d("Fence $key and $entryKey could NOT be removed.")
                                }
                            })
                }
            }
        }
    }

}