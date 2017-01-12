package com.github.tehras.workmode.ui.preferencesetup.fragmentcommon

import com.github.tehras.workmode.ui.FragmentScope
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsFragment
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = arrayOf(VolumeFragmentModule::class))
interface VolumeFragmentComponent {

    fun injectTo(fragment: VolumeSettingsListFragment)
    fun injectTo(fragment: VolumeNewSettingsFragment)

}