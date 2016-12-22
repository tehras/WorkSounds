package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.tehras.workmode.R

/**
 * A placeholder fragment containing a simple view.
 */
class VolumeNewSettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_volume, container, false)
    }
}
