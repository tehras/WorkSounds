package com.github.tehras.workmode.ui.dndpopup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.setButtonColor
import kotlinx.android.synthetic.main.dialog_dnd_popup.*

class DnDDialogFragment : DialogFragment() {

    companion object {
        fun instance(): DnDDialogFragment {
            return DnDDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_dnd_popup, container, false)
    }

    override fun onStart() {
        super.onStart()

        isCancelable = false

        dnd_enable_button.setButtonColor(R.color.colorAccent)
        dnd_skip_button.setButtonColor(android.R.color.white)

        dnd_enable_button?.setOnClickListener {
            this.dismiss()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                startActivity(Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
        dnd_skip_button?.setOnClickListener {
            this.dismiss()
        }
    }

}