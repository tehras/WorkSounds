package com.github.tehras.workmode.services

import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.Icon
import android.os.Build
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.github.tehras.workmode.extensions.EventType
import com.github.tehras.workmode.extensions.logEvent
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.services.ServiceHelper.isCurrentlyEnabled
import com.github.tehras.workmode.services.ServiceHelper.soundUpdated
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.splashscreen.SplashScreen
import timber.log.Timber
import java.util.*


abstract class PreferenceBaseTileService : TileService() {
    companion object {
        val BROADCAST_ACTION_REFRESH = "com.github.tehras.broadcast.refresh"
    }

    // handler for received data from service
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("onReceive")
            if (intent.action == BROADCAST_ACTION_REFRESH) {
                tileAddedOrStartListening()
            }
        }
    }

    override fun onStopListening() {
        val bm = LocalBroadcastManager.getInstance(this)
        bm.unregisterReceiver(mBroadcastReceiver)

        super.onStopListening()
    }

    override fun onStartListening() {
        super.onStartListening()
        Timber.i("onStartListening")

        tileAddedOrStartListening()

        val filter = IntentFilter()
        filter.addAction(BROADCAST_ACTION_REFRESH)
        val bm = LocalBroadcastManager.getInstance(this)
        bm.registerReceiver(mBroadcastReceiver, filter)
    }


    private fun tileAddedOrStartListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //check if there's more than 1 service
            val scene = getCorrectScene()

            Timber.d("scene - $scene")
            logEvent(EventType.TILE_EVENT, "Tile for ${this.javaClass.simpleName} Started Listening")

            if (scene != null) {
                updateTile(scene)
                //set active or inactive
                if (isCurrentlyEnabled(this, scene))
                    qsTile.state = Tile.STATE_ACTIVE
                else qsTile.state = Tile.STATE_INACTIVE
            } else {
                qsTile.state = Tile.STATE_UNAVAILABLE
            }
            qsTile.updateTile()
        }
    }

    @RequiresApi(24)
    private fun updateTile(scene: ScenePreference) {
        if (qsTile == null)
            requestListeningState(this, ComponentName(this, this.javaClass))
        qsTile?.let {
            qsTile.label = scene.name
            qsTile.icon = Icon.createWithResource(this.applicationContext, scene.selectedTile.tile)
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Timber.i("onTileAdded")

        tileAddedOrStartListening()
    }

    private fun getCorrectScene(): ScenePreference? {
        return getScene(ScenePreferenceSettings.getAllScenes(getPreferences()))
    }

    abstract fun getScene(scenes: ArrayList<ScenePreference>): ScenePreference?


    override fun onClick() {
        super.onClick()

        val scene = getCorrectScene()

        Timber.d("onClick - $scene")

        logEvent(EventType.TILE_EVENT, "Tile for ${this.javaClass.simpleName} Clicked")

        scene?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return
            } else {
                if (!(this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted) {
                    Toast.makeText(this, "Cannot change due to DND mode", Toast.LENGTH_SHORT).show()
                    //start app with permissions
                    startSplashScreen()
                } else {
                    when (qsTile.state) {
                        Tile.STATE_ACTIVE -> {
                            Timber.d("activated")
                            ServiceHelper.disableScene(it, this) {
                                soundUpdated()
                            }
                            qsTile.state = Tile.STATE_INACTIVE
                        }
                        Tile.STATE_INACTIVE -> {
                            Timber.d("deactivated")
                            ServiceHelper.enableScene(it, this, getPreferences()) {
                                soundUpdated()
                            }
                            qsTile.state = Tile.STATE_ACTIVE
                        }
                        Tile.STATE_UNAVAILABLE -> {
                            Timber.d("unavailable")
                            startSplashScreen()
                        }
                    }

                    updateTile(scene)

                    qsTile.updateTile()
                }
            }
        }

        if (scene == null) {
            startSplashScreen()
        }
    }

    private fun startSplashScreen() {
        this.startActivity(android.content.Intent(this, SplashScreen::class.java)) //add extra
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }
}