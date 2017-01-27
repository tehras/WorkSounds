package com.github.tehras.workmode.ui.permissions

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.*
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import kotlinx.android.synthetic.main.activity_permissions.*

class PermissionsActivity : AppCompatActivity() {

    val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
    }

    override fun onStart() {
        super.onStart()

        location_enable_button.setButtonColor(android.R.color.white)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location_enable_button.setOnClickListener {
                //ask for permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
            help_text.text = this.resources.getText(R.string.location_help_text)
            location_icon.setImageResource(R.drawable.ic_location)
        } else if (needsNotificationPermissions()) {
            showNeedsNotificationPermissionsLayout()
        } else {
            startListActivity()
        }

    }

    private fun showNeedsNotificationPermissionsLayout() {
        location_enable_button.setOnClickListener {
            showEnableNotificationsScreen()
        }
        help_text.text = this.resources.getText(R.string.dnd_explanation)
        location_icon.setImageResource(R.drawable.ic_dnd)
        location_icon.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                location_icon.removeOnLayoutChangeListener(this)
                location_icon.circularReveal()
            }
        })
    }

    private fun needsNotificationPermissions(): Boolean {
        var needsNotificationPermissions = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                needsNotificationPermissions = true
            }
        }
        return needsNotificationPermissions
    }

    private fun showEnableNotificationsScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is canceled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (needsNotificationPermissions()) {
                        showNeedsNotificationPermissionsLayout()
                    } else
                        startListActivity()
                } else {
                    //we really need this permission
                    Snackbar.make(this.getRootView(), "Location Permission is Required", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startListActivity() {
        val i = Intent(this, VolumeActivity::class.java)

        i.putExtra(EXTRA_ANIMATE, true)
        i.putExtra(EXTRA_X_COORDINATE, location_enable_button.centerX())
        i.putExtra(EXTRA_Y_COORDINATE, (location_enable_button.centerY())) //this had to be done to adjust for the toolbar
        i.putExtra(EXTRA_RADIUS_COORDINATE, (location_enable_button.right - location_enable_button.left) / 2)

        // permission was granted! Start new Activity
        startActivity(i)
        finish()
    }
}
