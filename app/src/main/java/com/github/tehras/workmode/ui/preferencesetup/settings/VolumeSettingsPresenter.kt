package com.github.tehras.workmode.ui.preferencesetup.settings

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.SwitchCompat
import android.widget.Button
import com.github.tehras.workmode.ui.base.Presenter

interface VolumeSettingsPresenter : Presenter<VolumeSettingsView> {
    fun setUpNotificationSettings(notifications_switch: SwitchCompat?)
    fun setUpLocationSettings(location_edit_text: AppCompatEditText?)
    fun setUpUpdateButton(update_button: Button?)
    fun setUpCancelButton(cancel_button: AppCompatButton?)
}