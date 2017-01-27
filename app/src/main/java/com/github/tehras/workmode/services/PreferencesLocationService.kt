package com.github.tehras.workmode.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.NotificationCompat
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_ENTRY_KEY
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_EXIT_KEY
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_KEY
import com.github.tehras.workmode.ui.splashscreen.SplashScreen
import com.google.android.gms.awareness.fence.FenceState
import timber.log.Timber


class PreferencesLocationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val fenceState = FenceState.extract(intent)

        Timber.d("Fence Receiver Received")

        if (fenceState.fenceKey.startsWith(FENCE_RECEIVER_ACTION_KEY, true)) {
            Timber.d("Fence entered -> ${fenceState.fenceKey}")
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    Timber.d("Fence > Currently at work.")
                    enableScene(getScene(fenceState.fenceKey.replace(FENCE_RECEIVER_ACTION_KEY, ""), context), context = context)
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
                    enableScene(getScene(fenceState.fenceKey.replace(FENCE_RECEIVER_ACTION_ENTRY_KEY, ""), context), context = context)
                }
                FenceState.FALSE -> {
                    Timber.d("Fence > Currently NOT at work.")
                }
                FenceState.UNKNOWN -> {
                    Timber.d("Fence > Current Location UNKNOWN.")
                }
            }
        } else if (fenceState.fenceKey.startsWith(FENCE_RECEIVER_ACTION_EXIT_KEY, true)) {
            Timber.d("Fence exited -> ${fenceState.fenceKey}")
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    val scene = getScene(fenceState.fenceKey.replace(FENCE_RECEIVER_ACTION_EXIT_KEY, ""), context)
                    scene?.let {
                        ServiceHelper.disableScene(scene, context) {
                            showLeftNotification(context, scene = scene)
                        }
                    }
                    Timber.d("Fence > Currently exiting work.")
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

    private fun getScene(substringAfter: String, context: Context?): ScenePreference? {
        val scenes = ScenePreferenceSettings.getAllScenes(getPreferences(context))
        scenes.forEach {
            if (it.id.toString().equals(substringAfter, true))
                return it
        }

        return null
    }

    private fun enableScene(scene: ScenePreference?, context: Context?) {
        scene?.let {
            ServiceHelper.enableScene(it, context, getPreferences(context)) {
                showNotification(context, it)
            }
        }
    }

    private fun showNotification(context: Context?, scene: ScenePreference) {
        context?.let {
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(scene.selectedTile.blackTile)
                    .setContentTitle("Scene sound was switched to Media - ${scene.inMediaVolume?.setMusicVolume ?: 0}/${scene.inMediaVolume?.maxMusicVolume} and Ring - ${scene.inRingVolume?.setMusicVolume ?: 0}/${scene.inRingVolume?.maxMusicVolume}")
                    .setContentText("${scene.name}")

            val resultIntent = Intent(context, SplashScreen::class.java)

            val resultPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder.setContentIntent(resultPendingIntent)
            // Sets an ID for the notification
            val mNotificationId = 1
            // Gets an instance of the NotificationManager service
            val mNotifyMgr = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, builder.build())
        }
    }

    private fun showLeftNotification(context: Context?, scene: ScenePreference) {
        context?.let {
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(scene.selectedTile.blackTile)
                    .setContentTitle("Scene was switched out - ${scene.outMediaVolume?.setMusicVolume ?: 0}/${scene.outMediaVolume?.maxMusicVolume} and Ring - ${scene.outRingVolume?.setMusicVolume ?: 0}/${scene.outRingVolume?.maxMusicVolume}")
                    .setContentText("${scene.name} Left")

            val resultIntent = Intent(context, SplashScreen::class.java)

            val resultPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder.setContentIntent(resultPendingIntent)
            // Sets an ID for the notification
            val mNotificationId = 1
            // Gets an instance of the NotificationManager service
            val mNotifyMgr = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, builder.build())
        }
    }

    private fun getPreferences(context: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context?.applicationContext)
    }
}