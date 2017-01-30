package com.github.tehras.workmode.ui.splashscreen

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.tehras.workmode.services.PreferencesLocationService
import com.github.tehras.workmode.services.PreferencesLocationService.Companion.clearNotification
import com.github.tehras.workmode.services.PreferencesLocationService.Companion.getScene
import com.github.tehras.workmode.services.ServiceHelper
import com.github.tehras.workmode.ui.permissions.PermissionsActivity
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import timber.log.Timber

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //check if it's coming from notificaiton
        if (PreferencesLocationService.NOTIFICATION_REPLY.equals(intent?.action ?: "", true)) {
            Timber.d("Notification Reply")
            //get scene id
            val id = intent?.getStringExtra(PreferencesLocationService.NOTIFICATION_SCENE_ID) ?: ""
            val scene = getScene(id, this)

            scene?.let { scene ->
                ServiceHelper.disableScene(scene, this) {} //leave empty
            }

            Toast.makeText(this, "Notification undone!", Toast.LENGTH_LONG).show()

            clearNotification(this)
        } else if (PreferencesLocationService.NOTIFICATION_REPLY_FROM_LEFT.equals(intent?.action ?: "", true)) {
            Timber.d("Notification Reply")
            //get scene id
            val id = intent?.getStringExtra(PreferencesLocationService.NOTIFICATION_SCENE_ID) ?: ""
            val scene = getScene(id, this)

            scene?.let {
                ServiceHelper.enableScene(it, this, PreferenceManager.getDefaultSharedPreferences(this.applicationContext), false) {} //leave empty
            }

            Toast.makeText(this, "Notification undone!", Toast.LENGTH_LONG).show()

            clearNotification(this)
        }

        //for now let's just launch the activity i have
        if (hasNotGivenPermissions())
            startActivity(Intent(this, PermissionsActivity::class.java))
        else
            startActivity(Intent(this, VolumeActivity::class.java))

        finish()
    }

    private fun hasNotGivenPermissions(): Boolean {
        var doesNotHavePermission = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                doesNotHavePermission = true
            }
        }
        return doesNotHavePermission || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }
}
