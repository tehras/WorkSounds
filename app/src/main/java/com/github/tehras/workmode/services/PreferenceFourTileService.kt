package com.github.tehras.workmode.services

import com.github.tehras.workmode.models.scene.ScenePreference
import java.util.*

class PreferenceFourTileService : PreferenceBaseTileService() {

    override fun getScene(scenes: ArrayList<ScenePreference>): ScenePreference? {
        return scenes.getOrNull(2)
    }

}