package com.github.tehras.workmode.ui.permissions

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
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
        location_enable_button.setOnClickListener {
            //ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is canceled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val i = Intent(this, VolumeActivity::class.java)

                    i.putExtra(EXTRA_ANIMATE, true)
                    i.putExtra(EXTRA_X_COORDINATE, location_enable_button.centerX())
                    i.putExtra(EXTRA_Y_COORDINATE, (location_enable_button.centerY())) //this had to be done to adjust for the toolbar
                    i.putExtra(EXTRA_RADIUS_COORDINATE, (location_enable_button.right - location_enable_button.left) / 2)

                    // permission was granted! Start new Activity
                    startActivity(i)
                    finish()
                } else {
                    //we really need this permission
                    Snackbar.make(this.getRootView(), "Location Permission is Required", Snackbar.LENGTH_LONG)
                }
            }
        }
    }
}
