package com.github.tehras.workmode.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.NotificationCompat
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.EventType
import com.github.tehras.workmode.extensions.logEvent
import com.github.tehras.workmode.models.generalsettings.getGeneralSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.ServiceHelper.soundUpdated
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeServiceInitHelper.Companion.FENCE_RECEIVER_ACTION_ENTRY_KEY
import com.github.tehras.workmode.ui.splashscreen.SplashScreen
import com.google.android.gms.awareness.fence.FenceState
import timber.log.Timber


class PreferencesLocationService : BroadcastReceiver() {
    companion object {
        val NOTIFICATION_REPLY = "com.github.tehras.workmode.service.notification.reply"
        val NOTIFICATION_REPLY_FROM_LEFT = "com.github.tehras.workmode.service.notification.reply.from.left"
        val NOTIFICATION_SCENE_ID = "args_scene_id"
        val NOTIFICATION_CODE = 199
        val NOTIFICATION_ID = 2

        fun getScene(substringAfter: String, context: Context?): ScenePreference? {
            val scenes = ScenePreferenceSettings.getAllScenes(getPreferences(context))

            Timber.d("Trying to get scene - $substringAfter")
            scenes.forEach {
                if (it.id.toString().equals(substringAfter, true))
                    return it
            }

            return null
        }

        fun clearNotification(context: Context?) {
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIFICATION_ID)
        }

        private fun getPreferences(context: Context?): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context?.applicationContext)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val fenceState = FenceState.extract(intent)

        Timber.d("Fence Receiver Received")

        if (NOTIFICATION_REPLY.equals(intent?.action ?: "", true)) {
            Timber.d("Notification Reply")
            //get scene id
            val id = intent?.getStringExtra(NOTIFICATION_SCENE_ID) ?: ""
            val scene = getScene(id, context)

            scene?.let {
                ServiceHelper.disableScene(it, context) {} //leave empty
            }
            context?.soundUpdated()
            clearNotification(context)
        } else if (NOTIFICATION_REPLY_FROM_LEFT.equals(intent?.action ?: "", true)) {
            Timber.d("Notification Reply")
            //get scene id
            val id = intent?.getStringExtra(NOTIFICATION_SCENE_ID) ?: ""
            val scene = getScene(id, context)

            scene?.let {
                ServiceHelper.enableScene(it, context, getPreferences(context), false) {} //leave empty
            }
            context?.soundUpdated()
            clearNotification(context)
        } else if (fenceState.fenceKey.equals("HeadphonesFence", true)) {
            Timber.i("Headphones Plugged In - ${fenceState.currentState}")
        } else if (fenceState.fenceKey.startsWith(FENCE_RECEIVER_ACTION_ENTRY_KEY, true)) {
            logEvent(EventType.LOCATION_EVENT, "Fence event received - entering location")

            Timber.d("Fence entered -> ${fenceState.fenceKey}")
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    Timber.d("Fence > Currently at work.")
                    enableScene(getScene(fenceState.fenceKey.replace("${FENCE_RECEIVER_ACTION_ENTRY_KEY}_", ""), context), context = context)
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

    private fun enableScene(scene: ScenePreference?, context: Context?) {
        Timber.d("trying to enable Scene - $scene")
        scene?.let {
            ServiceHelper.enableScene(it, context, getPreferences(context), false) {
                showNotification(context, it)
                context?.soundUpdated()
            }
        }
    }

    private fun showNotification(context: Context?, scene: ScenePreference) {
        if (!getGeneralSettings(getPreferences(context)).enableNotifications) {
            return
        }

        logEvent(EventType.LOCATION_EVENT, "Showing Enter Location")

        context?.let {
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(scene.selectedTile.blackTile)
                    .setContentTitle("Preferences adjusted - ${if (scene.inMediaVolume != null) "volume: ${convertToPercent(scene.inMediaVolume?.setMusicVolume ?: 0, scene.inMediaVolume?.maxMusicVolume ?: 0)}% " else ""} ${if (scene.inRingVolume != null) "media: ${convertToPercent(scene.inRingVolume?.setMusicVolume ?: 0, scene.inRingVolume?.maxMusicVolume ?: 0)}% " else ""}${if (scene.wifiEnabled) "Wi-Fi ${if (scene.wifiState) "enabled" else "disabled"} " else ""}")
                    .setContentText("Entered ${scene.name}")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setDefaults(Notification.DEFAULT_VIBRATE)

            val undoAction = android.support.v4.app.NotificationCompat.Action.Builder(R.drawable.ic_undo, "Undo", createPendingIntent(context, scene, false)).build()
            builder.addAction(undoAction)

            val resultIntent = Intent(context, SplashScreen::class.java)

            val resultPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder.setContentIntent(resultPendingIntent)
            // Gets an instance of the NotificationManager service
            val mNotifyMgr = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Builds the notification and issues it.
            mNotifyMgr.notify(NOTIFICATION_ID, builder.build())
        }
    }


    private fun createPendingIntent(context: Context?, scene: ScenePreference, leftScene: Boolean): PendingIntent? {
        context?.let {
            val intent: Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // start a
                // (i)  broadcast receiver which runs on the UI thread or
                // (ii) service for a background task to b executed , but for the purpose of
                // this codelab, will be doing a broadcast receiver
                intent = Intent(context, PreferencesLocationService::class.java)

                if (leftScene)
                    intent.action = NOTIFICATION_REPLY_FROM_LEFT
                else
                    intent.action = NOTIFICATION_REPLY

                intent.putExtra(NOTIFICATION_SCENE_ID, scene.id.toString())

                return PendingIntent.getBroadcast(it.applicationContext, NOTIFICATION_CODE, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                // start your activity for Android M and below
                intent = Intent(context, SplashScreen::class.java)
                if (leftScene)
                    intent.action = NOTIFICATION_REPLY_FROM_LEFT
                else
                    intent.action = NOTIFICATION_REPLY
                intent.putExtra(NOTIFICATION_SCENE_ID, scene)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return PendingIntent.getActivity(it.applicationContext, NOTIFICATION_CODE, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        return null
    }

    private fun convertToPercent(set: Int, max: Int): String {
        return set.toDouble().times(100.0).div(max.toDouble()).toInt().toString()
    }
}