package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.addSimpleTextChangeListener
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.models.scene.*
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

    override fun setUpName(nameField: EditText?) {
        nameField?.addSimpleTextChangeListener {
            scenePreference.name = it
            view?.showNameNeedsToBeSelected(false)
        }
    }

    override fun setUpInVolumeControls(linearLayout: LinearLayout?) {
        //ring volume setup
        linearLayout?.let {
            val ringVolume = linearLayout.findViewById(R.id.ring_volume) as VolumeProgressLayout
            val mediaVolume = linearLayout.findViewById(R.id.media_volume) as VolumeProgressLayout

            //media volume setup
            mediaVolume.onProgressChangeListener { current, max ->
                scenePreference.inMediaVolume = AudioSettings(max, current)
            }
            ringVolume.onProgressChangeListener { current, max ->
                scenePreference.inRingVolume = AudioSettings(max, current)
            }
        }
    }

    override fun setUpOutVolumeControls(linearLayout: LinearLayout?) {
        //ring volume setup
        linearLayout?.let {
            val radioDoNothing = linearLayout.findViewById(R.id.radio_group_do_nothing) as AppCompatRadioButton
            val radioDoPrevious = linearLayout.findViewById(R.id.radio_go_back_to_previous) as AppCompatRadioButton
            val radioSetCustom = linearLayout.findViewById(R.id.radio_set_custom) as AppCompatRadioButton
            val ringVolume = linearLayout.findViewById(R.id.custom_ring_volume) as VolumeProgressLayout
            val mediaVolume = linearLayout.findViewById(R.id.custom_media_volume) as VolumeProgressLayout

            val updatePreference = { preference: AudioSetVolumePreference -> updateSelectedPreference(preference, ringVolume, mediaVolume, radioDoNothing, radioDoPrevious, radioSetCustom) }

            radioDoNothing.setOnClickListener { updatePreference(AudioSetVolumePreference.DO_NOTHING) }
            radioDoPrevious.setOnClickListener { updatePreference(AudioSetVolumePreference.BACK_TO_PREVIOUS) }
            radioSetCustom.setOnClickListener { updatePreference(AudioSetVolumePreference.CUSTOM) }

            updatePreference(scenePreference.outMediaPreferenceSelected)

            ringVolume.onProgressChangeListener { current, max ->
                scenePreference.inRingVolume = AudioSettings(max, current)
            }
            mediaVolume.onProgressChangeListener { current, max ->
                scenePreference.inMediaVolume = AudioSettings(max, current)
            }
        }
    }

    private fun updateSelectedPreference(selectedPreference: AudioSetVolumePreference, ringVolume: VolumeProgressLayout, mediaVolume: VolumeProgressLayout, radioDoNothing: AppCompatRadioButton, radioDoPrevious: AppCompatRadioButton, radioSetCustom: AppCompatRadioButton) {
        scenePreference.outMediaPreferenceSelected = selectedPreference

        //reset all button
        Timber.d("updateSelectedPreference - > $selectedPreference")

        radioDoNothing.isChecked = false
        radioDoPrevious.isChecked = false
        radioSetCustom.isChecked = false

        when (selectedPreference) {
            AudioSetVolumePreference.CUSTOM -> {
                showVolume(ringVolume, mediaVolume)
                radioSetCustom.isChecked = true
            }
            AudioSetVolumePreference.BACK_TO_PREVIOUS -> {
                hideVolume(ringVolume, mediaVolume)
                radioDoPrevious.isChecked = true
            }
            AudioSetVolumePreference.DO_NOTHING -> {
                hideVolume(ringVolume, mediaVolume)
                radioDoNothing.isChecked = true
            }
        }
    }


    /**
     * Hide Ring and Media Volumes
     */
    private fun hideVolume(ringVolume: VolumeProgressLayout, mediaVolume: VolumeProgressLayout) {
        ringVolume.visibility = View.GONE
        ringVolume.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_out_bottom))
        mediaVolume.visibility = View.GONE
        mediaVolume.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_out_bottom))
    }

    /**
     * Show Ring and Media Volumes
     */
    private fun showVolume(ringVolume: VolumeProgressLayout, mediaVolume: VolumeProgressLayout) {
        ringVolume.visibility = View.VISIBLE
        ringVolume.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_in_bottom))
        mediaVolume.visibility = View.VISIBLE
        mediaVolume.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_in_bottom))
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

    override fun setUpButtonBar(cancelButton: AppCompatButton?, createButton: AppCompatButton?) {
        cancelButton?.setButtonColor(android.R.color.white)
        createButton?.setButtonColor(android.R.color.white)

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
        } else {
            //save the request
            ScenePreferenceSettings.saveScene(scenePreference, preferences)

            //notify view everything went through fine
            view?.notifySceneSubmitted()
        }
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