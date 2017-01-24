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

    fun updateScene(scene: ScenePreference, preference: SharedPreferences) {
        val scenes = getAllScenes(preference)

        val changed = scenes.repeatUntil({ it == scene }) { it.update(scene) }

        if (changed)
            preference.edit().putString(KEY_SCENE_PREFERENCE_OBJ, convertToJson(scenes)).apply()
    }

    fun deleteScene(scene: ScenePreference, preference: SharedPreferences) {
        val scenes = getAllScenes(preference)

        val changed = scenes.repeatUntil({ it == scene }) { scenes.remove(it) }

        if (changed)
            preference.edit().putString(KEY_SCENE_PREFERENCE_OBJ, convertToJson(scenes)).apply()

    }

    inline fun <T> ArrayList<T>.repeatUntil(equalCondition: (T) -> Boolean, task: (T) -> Unit): Boolean {
        this.forEach {
            if (equalCondition(it)) {
                task(it)
                return true
            }
        }

        return false
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