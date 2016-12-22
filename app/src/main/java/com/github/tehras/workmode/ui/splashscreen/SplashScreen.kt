package com.github.tehras.workmode.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.tehras.workmode.ui.preferencesetup.VolumeActivity
import com.github.tehras.workmode.ui.work.WorkActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo decide which activity to launch
        //for now let's just launch the activity i have
        if (hasNotSelectedVolumeSettings()) {
            startActivity(Intent(this, VolumeActivity::class.java))
        } else {
            startActivity(Intent(this, WorkActivity::class.java))
        }
        finish()
    }

    private fun hasNotSelectedVolumeSettings(): Boolean {
        //todo add logic
        return false
    }
}
