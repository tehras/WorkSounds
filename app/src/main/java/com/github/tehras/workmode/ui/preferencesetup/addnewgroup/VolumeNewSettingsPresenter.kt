package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.LinearLayout
import com.github.tehras.workmode.ui.base.Presenter

interface VolumeNewSettingsPresenter : Presenter<VolumeNewSettingsView> {
    fun setUpHorizontalImagePicker(imageSelectorView: RecyclerView)
    fun setUpInVolumeControls(linearLayout: LinearLayout?)
    fun setUpOutVolumeControls(linearLayout: LinearLayout?)
    fun setUpName(nameField: EditText?)
    fun setUpButtonBar(cancelButton: AppCompatButton?, createButton: AppCompatButton?)
}