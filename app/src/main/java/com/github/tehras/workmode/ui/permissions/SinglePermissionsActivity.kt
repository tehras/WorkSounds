package com.github.tehras.workmode.ui.permissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.ViewTreeObserver
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.*
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import kotlinx.android.synthetic.main.activity_permissions.*

class SinglePermissionsActivity : AppCompatActivity() {

    companion object {
        private val ARG_PERMISSION_KEY = "arg_permission_kiey"
        private val ARG_PERMISSION_TO_ASK_KEY = "arg_permission_kiey"
        val VALUE_PERMISSION_WIFI: Int = 0
        val PERMISSION_REQUEST_CODE: Int = 2001

        private fun startSinglePermissionActivity(activity: Activity, permission: Int, permissionToAsk: String) {
            val i = Intent(activity, VolumeActivity::class.java)
            i.putExtra(ARG_PERMISSION_KEY, permission)
            i.putExtra(ARG_PERMISSION_TO_ASK_KEY, permission)

            activity.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permission = intent.getIntExtra(ARG_PERMISSION_KEY, -1)
        val permissionToAsk = intent.getStringExtra(ARG_PERMISSION_KEY)

        setContentView(R.layout.activity_permissions)

        var text: Spanned? = null
        @IntegerRes
        var icon: Int? = null

        when (permission) {
            VALUE_PERMISSION_WIFI -> {
                @Suppress("DEPRECATION")
                text = Html.fromHtml(this.resources.getText(R.string.wifi_permission).toString())
                icon = R.drawable.ic_wifi
            }
        }

        location_enable_button.setButtonColor(android.R.color.white)
        location_enable_button.setOnClickListener {
            //ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(permissionToAsk), PERMISSION_REQUEST_CODE)
        }
        text?.let {
            help_text.text = it
        }
        icon?.let {
            location_icon.setImageResource(it)
        }

        animateIn(true)
    }

    private fun animateIn(firstLoad: Boolean) {
        val reveal: () -> Unit = { location_icon.circularReveal() }

        if (firstLoad) {
            location_icon.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    reveal()
                    location_icon.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            reveal()
        }
        location_enable_button.animateFromBottom()
        help_text.animateFromRight()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is canceled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    //we really need this permission
                    logError(Exception("Permission is Required"))
                    Snackbar.make(this.getRootView(), "Permission is Required", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

}