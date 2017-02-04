package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.support.v7.widget.AppCompatButton
import android.view.View
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.setButtonColor
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.AbstractViewHolder


class VolumeSettingsSuggestion(val view: View, val sendASuggestion: () -> Unit) : AbstractViewHolder<ScenePreference>(view) {
    override fun bindView(t: ScenePreference?) {
        //scene doesn't matter here
        val button = (view.findViewById(R.id.submit_button) as AppCompatButton)
        button.setButtonColor(android.R.color.white)

        button.setOnClickListener {
            sendASuggestion()
        }
    }

}
