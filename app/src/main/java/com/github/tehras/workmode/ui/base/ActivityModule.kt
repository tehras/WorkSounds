package com.github.tehras.workmode.ui.base

import android.content.Context
import com.github.tehras.workmode.ui.ActivityScope
import dagger.Module
import dagger.Provides

@Module
abstract class ActivityModule(private val activity: BaseActivity) {
    @Provides @ActivityScope
    fun provideActivity(): BaseActivity = activity

    @Provides @ActivityScope
    fun provideActivityContext(): Context = activity.baseContext
}