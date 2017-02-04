package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.Presenter
import com.google.android.gms.location.places.Place

interface VolumeNewSettingsPresenter : Presenter<VolumeNewSettingsView> {
    fun setUpHorizontalImagePicker(imageSelectorView: RecyclerView)
    fun setUpVolumeControls(linearLayout: View?)
    fun setUpName(nameField: EditText?)
    fun setUpButtonBar(cancelButton: AppCompatButton?, createButton: AppCompatButton?)
    fun setUpLocation(locationLayout: LinearLayout?)
    fun saveLocation(place: Place)
    fun setEditLayout(scenePreference: ScenePreference)
    fun setUpWiFiControls(wifiLayout: View?)
}