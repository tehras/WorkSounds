package com.github.tehras.workmode.ui.preferencesetup.fragmentcommon

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.github.tehras.workmode.ui.FragmentScope
import com.github.tehras.workmode.ui.base.BaseFragment
import com.github.tehras.workmode.ui.base.FragmentModule
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsPresenter
import com.github.tehras.workmode.ui.preferencesetup.addnewgroup.VolumeNewSettingsPresenterImpl
import com.github.tehras.workmode.ui.preferencesetup.settings.VolumeSettingsPresenter
import com.github.tehras.workmode.ui.preferencesetup.settings.VolumeSettingsPresenterImpl
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListPresenter
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class VolumeFragmentModule(fragment: BaseFragment) : FragmentModule(fragment = fragment) {

    @Provides @FragmentScope
    fun provideVolumePresenter(presenter: VolumeSettingsListPresenterImpl): VolumeSettingsListPresenter = presenter

    @Provides @FragmentScope
    fun provideNewVolumePresenter(presenter: VolumeNewSettingsPresenterImpl): VolumeNewSettingsPresenter = presenter

    @Provides @FragmentScope
    fun provideVolumeSettingsPresenter(presenter: VolumeSettingsPresenterImpl): VolumeSettingsPresenter = presenter

    @Provides @FragmentScope
    fun provideSharedPreference(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.activity.applicationContext)
}