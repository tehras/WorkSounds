package com.github.tehras.workmode.ui.work

import com.github.tehras.workmode.ui.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = arrayOf(WorkModule::class))
interface WorkComponent {

    fun injectTo(activity: WorkActivity)
}