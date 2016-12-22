package com.github.tehras.workmode.ui.preferencesetup.fragmentcommon

import com.github.tehras.workmode.ui.FragmentScope
import com.github.tehras.workmode.ui.base.BaseFragment
import com.github.tehras.workmode.ui.base.FragmentModule
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListPresenter
import com.github.tehras.workmode.ui.preferencesetup.volumesettingslist.VolumeSettingsListPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class VolumeFragmentModule(var fragment: BaseFragment) : FragmentModule(fragment = fragment) {


    @Provides @FragmentScope
    fun providePresenter(presenter: VolumeSettingsListPresenterImpl): VolumeSettingsListPresenter = presenter

}