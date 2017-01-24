package com.github.tehras.workmode.ui.splashscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.github.tehras.workmode.ui.permissions.PermissionsActivity
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for now let's just launch the activity i have
        if (hasNotGivenPermissions())
            startActivity(Intent(this, PermissionsActivity::class.java))
        else
            startActivity(Intent(this, VolumeActivity::class.java))

        finish()
    }

    private fun hasNotGivenPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

    }
}
