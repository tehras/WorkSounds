package com.github.tehras.workmode.ui.preferencesetup

import com.github.tehras.workmode.ui.ActivityScope
import dagger.Subcomponent


@ActivityScope
@Subcomponent(modules = arrayOf(VolumeModule::class))
interface VolumeComponent {

    fun injectTo(activity: VolumeActivity)

}