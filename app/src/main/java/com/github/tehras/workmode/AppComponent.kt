package com.github.tehras.workmode

import com.github.tehras.workmode.ui.work.WorkComponent
import com.github.tehras.workmode.ui.work.WorkModule
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
    fun plus(workModule: WorkModule): WorkComponent
}