package com.github.tehras.workmode.ui.work

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.github.tehras.workmode.ui.ActivityScope
import com.github.tehras.workmode.ui.base.ActivityModule
import com.github.tehras.workmode.ui.base.BaseActivity
import dagger.Module
import dagger.Provides

@Module
class WorkModule(val activity: BaseActivity) : ActivityModule(activity) {
    @Provides @ActivityScope
    fun providePresenter(presenter: WorkPresenterImpl): WorkPresenter = presenter

    @Provides @ActivityScope
    fun provideSharedPreference(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
}