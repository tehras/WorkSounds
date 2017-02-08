package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addSimpleTextChangeListener
import com.github.tehras.workmode.extensions.animateFromLeft
import com.github.tehras.workmode.extensions.animateOutToLeft
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.models.scene.AudioSettings
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.models.scene.TileImage
import com.github.tehras.workmode.models.scene.VolumePlace
import com.github.tehras.workmode.shared.ScenePreferenceSettings
import com.github.tehras.workmode.ui.base.AbstractPresenter
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.adapter.ImagePickerAdapter
import com.github.tehras.workmode.views.VolumeProgressLayout
import com.google.android.gms.location.places.Place
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("PrivateResource")
class VolumeNewSettingsPresenterImpl @Inject constructor(var preferences: SharedPreferences) : AbstractPresenter<VolumeNewSettingsView>(), VolumeNewSettingsPresenter {

    //If edit i'll have to add some logic here
    var scenePreference: ScenePreference = ScenePreference()
    var isEditLayout: Boolean = false

    override fun setEditLayout(scenePreference: ScenePreference) {
        this.scenePreference = scenePreference
        this.isEditLayout = true
    }

    override fun setUpName(nameField: EditText?) {
        if (isEditLayout)
            nameField?.setText(scenePreference.name)

        nameField?.setSingleLine(true)
        nameField?.imeOptions = EditorInfo.IME_ACTION_DONE
        nameField?.addSimpleTextChangeListener {
            scenePreference.name = it
            view?.showNameNeedsToBeSelected(false)
        }
    }

    override fun setUpVolumeControls(linearLayout: View?) {
        linearLayout?.let {
            val ringSwitch = it.findViewById(R.id.volume_enable_switch) as SwitchCompat?
            val mediaSwitch = it.findViewById(R.id.media_enable_switch) as SwitchCompat?

            val ringEnabled = scenePreference.isRingEnabled()
            val mediaEnabled = scenePreference.isMediaEnabled()

            ringSwitch?.isChecked = ringEnabled
            mediaSwitch?.isChecked = mediaEnabled

            val inRing = it.findViewById(R.id.volume_container) as LinearLayout?
            val inMedia = it.findViewById(R.id.media_container) as LinearLayout?

            if (ringEnabled) inRing?.visibility = View.VISIBLE else inRing?.visibility = View.GONE
            if (mediaEnabled) inRing?.visibility = View.VISIBLE else inRing?.visibility = View.GONE

            ringSwitch?.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    view?.showAtLeastOneNonMandatoryField(false)
                    initRing(it.context)
                    inRing?.animateFromLeft()
                } else {
                    inRing?.animateOutToLeft()
                    scenePreference.inRingVolume = null
                }
            }
            mediaSwitch?.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    view?.showAtLeastOneNonMandatoryField(false)
                    initMedia(it.context)
                    inMedia?.animateFromLeft()
                } else {
                    inMedia?.animateOutToLeft()
                    scenePreference.inMediaVolume = null
                }
            }

            setUpRingVolume(inRing)
            setUpMediaVolume(inMedia)
        }
    }

    private fun initRing(context: Context) {
        //get max
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        scenePreference.inRingVolume = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
    }

    private fun initMedia(context: Context) {
        //get max
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        scenePreference.inMediaVolume = AudioSettings(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
    }

    private fun setUpRingVolume(linearLayout: LinearLayout?) {
        //ring volume setup
        linearLayout?.let {
            val ringVolume = linearLayout.findViewById(R.id.ring_volume) as VolumeProgressLayout

            ringVolume.onProgressChangeListener { current, max ->
                scenePreference.inRingVolume = AudioSettings(max, current)
            }
            if (isEditLayout) {
                if (scenePreference.isRingEnabled()) {
                    ringVolume.setVolumeLevel(scenePreference.inRingVolume?.setMusicVolume ?: 0, scenePreference.inRingVolume?.maxMusicVolume ?: 0)
                }
            }
        }
    }

    private fun setUpMediaVolume(linearLayout: LinearLayout?) {
        //ring volume setup
        linearLayout?.let {
            val mediaVolume = linearLayout.findViewById(R.id.media_volume) as VolumeProgressLayout

            //media volume setup
            mediaVolume.onProgressChangeListener { current, max ->
                scenePreference.inMediaVolume = AudioSettings(max, current)
            }
            if (isEditLayout) {
                if (scenePreference.isMediaEnabled()) {
                    mediaVolume.setVolumeLevel(scenePreference.inRingVolume?.setMusicVolume ?: 0, scenePreference.inRingVolume?.maxMusicVolume ?: 0)
                }
            }
        }
    }

    override fun setUpWiFiControls(wifiLayout: View?) {
        wifiLayout?.let {
            val switch = it.findViewById(R.id.wifi_switch_enable) as SwitchCompat
            val layout = it.findViewById(R.id.wifi_container)

            val showLayout: () -> Unit = { layout.animateFromLeft() }
            val hideLayout: () -> Unit = { layout.animateOutToLeft() }

            if (scenePreference.wifiEnabled) {
                layout.visibility = View.VISIBLE
            } else layout.visibility = View.GONE

            switch.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    view?.showAtLeastOneNonMandatoryField(false)
                    showLayout()
                } else hideLayout()

                scenePreference.wifiEnabled = b
            }

            switch.isChecked = scenePreference.wifiEnabled

            val clickable = it.findViewById(R.id.wifi_enable_disable_layout)

            val icon = it.findViewById(R.id.wifi_enabled_disabled_image) as ImageView
            val text = it.findViewById(R.id.wifi_enable_disable_description) as TextView

            setWifi(!scenePreference.wifiState, icon, text)
            clickable.setOnClickListener {
                setWifi(scenePreference.wifiState, icon, text)

                scenePreference.wifiState = !scenePreference.wifiState
                Timber.d("scene wifi state changed to - ${scenePreference.wifiState}")
            }
        }
    }

    private fun setWifi(enabled: Boolean, icon: ImageView, text: TextView) {
        if (!enabled) {
            icon.setImageResource(R.drawable.ic_wifi)
            text.text = text.resources.getString(R.string.enable_wifi)
        } else {

            icon.setImageResource(R.drawable.ic_wifi_off)
            text.text = text.resources.getString(R.string.disable_wifi)
        }
    }

    /**
     * Image Selector Layout
     */
    override fun setUpHorizontalImagePicker(imageSelectorView: RecyclerView) {
        imageSelectorView.layoutManager = LinearLayoutManager(imageSelectorView.context, LinearLayoutManager.HORIZONTAL, false)
        imageSelectorView.setHasFixedSize(true)
        imageSelectorView.adapter = ImagePickerAdapter(scenePreference.selectedTile) {
            scenePreference.selectedTile = it
            view?.showTileNeedsToBeSelected(false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setUpButtonBar(cancelButton: AppCompatButton?, createButton: AppCompatButton?) {
        cancelButton?.setButtonColor(android.R.color.white)
        createButton?.setButtonColor(android.R.color.white)
        if (isEditLayout)
            createButton?.text = "Update"

        cancelButton?.setOnClickListener {
            view?.showCancelDialog()
        }
        createButton?.setOnClickListener {
            validateAndSubmitScene()
        }
    }

    private fun validateAndSubmitScene() {
        //validate scene preference
        if (scenePreference.selectedTile == TileImage.NONE) {
            view?.showTileNeedsToBeSelected(true)
        } else if (scenePreference.name.isNullOrEmpty()) {
            view?.showNameNeedsToBeSelected(true)
        } else if (scenePreference.location == null) {
            view?.showLocationNeedsToBeSelected(true)
        } else if (!atLeastOneNonMandatoryFieldSelected()) {
            view?.showAtLeastOneNonMandatoryField(true)
        } else {
            //save the request
            if (!isEditLayout)
                ScenePreferenceSettings.saveScene(scenePreference, preferences)
            else {
                ScenePreferenceSettings.updateScene(scenePreference, preferences)
            }

            //notify view everything went through fine
            view?.notifySceneSubmitted()
        }
    }

    private fun atLeastOneNonMandatoryFieldSelected(): Boolean {
        return scenePreference.wifiEnabled || scenePreference.isRingEnabled() || scenePreference.isMediaEnabled()
    }

    override fun setUpLocation(locationLayout: LinearLayout?) {
        if (scenePreference.location?.address.isNullOrEmpty()) {
            view?.showSelectLocation()
        } else {
            view?.showLocationExisted(scenePreference.location!!.address)
        }
    }

    override fun saveLocation(place: Place) {
        scenePreference.location = VolumePlace(place)
        view?.showLocationExisted(scenePreference.location?.address)
    }

}