package com.github.tehras.workmode.ui.base

import com.github.tehras.workmode.ui.FragmentScope
import dagger.Module
import dagger.Provides

@Module
abstract class FragmentModule(private val fragment: BaseFragment) {

    @Provides @FragmentScope
    fun provideActivity(): BaseFragment = fragment

}