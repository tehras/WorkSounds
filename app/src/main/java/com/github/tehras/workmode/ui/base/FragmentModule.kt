package com.github.tehras.workmode.ui.base

import android.content.Context
import com.github.tehras.workmode.ui.FragmentScope
import dagger.Module
import dagger.Provides

@Module
abstract class FragmentModule(protected val fragment: BaseFragment) {

    @Provides @FragmentScope
    fun provideFragment(): BaseFragment = fragment

    @Provides @FragmentScope
    fun provideContext(): Context = fragment.activity.applicationContext

}