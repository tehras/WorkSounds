package com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.listview

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.AppCompatButton
import android.view.View
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.circularReveal
import com.github.tehras.workmode.models.scene.ScenePreference
import com.github.tehras.workmode.ui.base.AbstractViewHolder


class VolumeSettingsSuggestion(val view: View) : AbstractViewHolder<ScenePreference>(view) {
    override fun bindView(t: ScenePreference?) {
        //scene doesn't matter here

        //animate in the question mark
        view.findViewById(R.id.help_icon).circularReveal()
        val button = (view.findViewById(R.id.submit_button) as AppCompatButton)
        button.setBackgroundColor(android.R.color.white)

        button.setOnClickListener {
            val emailIntent = Intent(android.content.Intent.ACTION_SEND)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("koshkinbrosdev@gmail.com"))
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Scenes App Suggestion")
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Please include .. ")
            startActivity(view.context, Intent.createChooser(emailIntent, "Send mail using..."), null)
        }
    }

}
