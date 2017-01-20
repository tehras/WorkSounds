package com.github.tehras.workmode.shared

import android.content.SharedPreferences
import com.github.tehras.workmode.models.scene.ScenePreference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object ScenePreferenceSettings {
    private val KEY_SCENE_PREFERENCE_OBJ = "key_scene_preference_obj"

    fun saveScene(scene: ScenePreference, preference: SharedPreferences) {
        preference.edit().putString(KEY_SCENE_PREFERENCE_OBJ, addScene(scene, preference)).apply()
    }

    fun getAllScenes(preference: SharedPreferences): ArrayList<ScenePreference> {
        val savedScenes = preference.getString(KEY_SCENE_PREFERENCE_OBJ, "")
        if (savedScenes.isNullOrEmpty()) {
            return ArrayList()
        } else {
            return Gson().fromJson(savedScenes, object : TypeToken<ArrayList<ScenePreference>>() {}.type)
        }
    }

    private fun addScene(scene: ScenePreference, preference: SharedPreferences): String? {
        val allScenes = getAllScenes(preference)
        allScenes.add(scene)

        return convertToJson(allScenes)
    }

    private fun convertToJson(preferences: List<ScenePreference>): String {
        return Gson().toJson(preferences)
    }

}