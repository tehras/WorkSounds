package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import com.github.tehras.workmode.models.generalsettings.getGeneralSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.PreferencesLocationService
import com.github.tehras.workmode.services.ServiceHelper.soundUpdated
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.base.BaseActivity
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallbacks
import com.google.android.gms.common.api.Status
import timber.log.Timber


class VolumeServiceInitHelper(val preferences: SharedPreferences, val activity: BaseActivity, val registerReceiver: (PreferencesLocationService, IntentFilter) -> Unit) {

    companion object {
        val FENCE_RECEIVER_ACTION = "com.github.tehras.workmode.locationreceiver.FENCE_RECEIVER_ACTION"
        val FENCE_RECEIVER_ACTION_ENTRY_KEY = "fence_receiver_entry_key"
    }

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFencePendingIntent: PendingIntent? = null
    private var fenceReceiver: PreferencesLocationService? = null
    private var initialized: Boolean = false

    init {
        initialize()
    }

    fun initialize() {
        Timber.d("initializing - $initialized")

        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)
        if (preferences.isNotEmpty() && !initialized) {
            //initialize
            mGoogleApiClient = GoogleApiClient.Builder(activity)
                    .addApi(Awareness.API)
                    .build()
            mGoogleApiClient?.connect()

            val intent = Intent(FENCE_RECEIVER_ACTION)
            mFencePendingIntent = PendingIntent.getBroadcast(activity.applicationContext,
                    0,
                    intent,
                    0)

            fenceReceiver = PreferencesLocationService()
            Timber.d("Registering receiver")
            registerReceiver(fenceReceiver!!, IntentFilter(FENCE_RECEIVER_ACTION))

            initialized = true
        }
    }


    fun unbind() {
//        unregisterFence()
//        unregisterReceiver
    }


    fun registerFence() {
        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)

        if (preferences.isNotEmpty()) {
            preferences.forEachIndexed { i, it ->
                Timber.d("locationEntering -> ${it.location?.location?.latitude}, ${it.location?.location?.longitude}")

                val radius = getGeneralSettings(preferences = this.preferences).locationRange.toDouble()
                Timber.d("radius -> $radius")

                val locationEnteringFence = LocationFence.entering(it.location?.location?.latitude ?: 0.00, it.location?.location?.longitude ?: 0.00, radius)

                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        FenceUpdateRequest.Builder()
                                .addFence(createEntryKey(it), locationEnteringFence, mFencePendingIntent)
                                .build())
                        .setResultCallback { status ->
                            if (status.isSuccess) {
                                Timber.d("Fence was successfully registered.")
                            } else {
                                Timber.d("Fence could not be registered: " + status)
                            }
                        }
            }

            activity.soundUpdated()
        }
    }

    private fun createEntryKey(it: ScenePreference): String {
        return "${FENCE_RECEIVER_ACTION_ENTRY_KEY}_${it.id}"
    }

    fun unregisterFence() {
        val preferences = ScenePreferenceSettings.getAllScenes(preference = preferences)

        if (preferences.isNotEmpty()) {
            preferences.forEach { pref ->
                val entryKey = createEntryKey(pref)
                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        FenceUpdateRequest.Builder()
//                                .removeFence("HeadphonesFence")
                                .removeFence(entryKey)
                                .build())
                        .setResultCallback(object : ResultCallbacks<Status>() {
                            override fun onSuccess(status: Status) {
                                Timber.d("Fence and $entryKey successfully removed.")
                            }

                            override fun onFailure(status: Status) {
                                Timber.d("Fence  $entryKey could NOT be removed.")
                            }
                        })
            }
        }
    }

}