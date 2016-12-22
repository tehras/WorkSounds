package com.github.tehras.workmode.ui.preferencesetup.fragmentcommon

import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsFragment
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListFragment
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(VolumeFragmentModule::class))
interface VolumeFragmentComponent {

    fun injectTo(fragment: VolumeNewSettingsFragment)
    fun injectTo(fragment: VolumeSettingsListFragment)

}