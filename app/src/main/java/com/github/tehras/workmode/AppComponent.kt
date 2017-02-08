package com.github.tehras.workmode

import com.github.tehras.workmode.ui.preferencesetup.VolumeComponent
import com.github.tehras.workmode.ui.preferencesetup.VolumeModule
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentComponent
import com.github.tehras.workmode.ui.preferencesetup.fragmentcommon.VolumeFragmentModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by tehras on 11/5/16.
 *
 * AppComponent will insert the appropriate modules
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun injectTo(app: MyApp)

    // Submodule methods
    // Every screen is its own submodule of the graph and must be added here.
    // fun plus(module: Module): ModuleComponent

    //    fun plus(homeLoanModule: HomeLoanModule): HomeLoanComponent
    fun plus(fragmentModule: VolumeFragmentModule): VolumeFragmentComponent
    fun plus(volumeModule: VolumeModule): VolumeComponent
}