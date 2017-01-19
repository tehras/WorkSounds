package com.github.tehras.workmode.shared

import android.content.SharedPreferences
import com.github.tehras.workmode.models.scene.ScenePreference

object ScenePreferenceSettings {
    val KEY_SCENE_PREFERENCE_OBJ = "key_scene_preference_obj"

    fun saveScene(scene: ScenePreference, preference: SharedPreferences) {
        preference.edit().putString(KEY_SCENE_PREFERENCE_OBJ, scene.toJson()).apply()
    }

}