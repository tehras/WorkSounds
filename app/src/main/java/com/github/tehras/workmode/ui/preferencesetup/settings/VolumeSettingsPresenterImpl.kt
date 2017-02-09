package com.github.tehras.workmode.ui.preferencesetup.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.SwitchCompat
import android.widget.Button
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addSimpleTextChangeListener
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.models.generalsettings.GeneralSettings
import com.github.tehras.workmode.models.generalsettings.getGeneralSettings
import com.github.tehras.workmode.models.generalsettings.updateGeneralSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import javax.inject.Inject

class VolumeSettingsPresenterImpl @Inject constructor(val preferences: SharedPreferences) : AbstractPresenter<VolumeSettingsView>(), VolumeSettingsPresenter {

    var settings: GeneralSettings? = null


    override fun bindView(view: VolumeSettingsView) {
        super.bindView(view)

        //get the preference object
        settings = getGeneralSettings(preferences)

    }

    override fun setUpNotificationSettings(notifications_switch: SwitchCompat?) {
        notifications_switch?.isChecked = settings?.enableNotifications ?: true

        notifications_switch?.setOnCheckedChangeListener { compoundButton, b ->
            settings?.enableNotifications = b
        }
    }

    override fun setUpCancelButton(cancel_button: AppCompatButton?) {
        cancel_button?.setButtonColor(android.R.color.white)
        cancel_button?.setOnClickListener { view?.close() }
    }

    override fun setUpUpdateButton(update_button: Button?) {
        update_button?.setButtonColor(R.color.colorPrimary)
        update_button?.setOnClickListener {
            updateGeneralSettings(settings, preferences)
            view?.close()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setUpLocationSettings(location_edit_text: AppCompatEditText?) {
        location_edit_text?.setText(settings?.locationRange?.toString() ?: 200.toString())
        location_edit_text?.addSimpleTextChangeListener {
            try {
                var range = it.toInt()
                if (range <= 100) {
                    range = 100
                } else if (range > 500) {
                    range = 500
                } else
                    settings?.locationRange = range
            } catch (e: Exception) {
                settings?.locationRange = 200
            }
        }
    }

}