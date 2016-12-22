package com.github.tehras.workmode.ui.preferencesetup

import com.github.tehras.workmode.ui.ActivityScope
import com.github.tehras.workmode.ui.base.ActivityModule
import com.github.tehras.workmode.ui.base.BaseActivity
import dagger.Module
import dagger.Provides

@Module
class VolumeModule(activity: BaseActivity) : ActivityModule(activity = activity) {

    @Provides @ActivityScope
    fun providePresenter(presenter: VolumeViewImpl): VolumePresenter = presenter

}