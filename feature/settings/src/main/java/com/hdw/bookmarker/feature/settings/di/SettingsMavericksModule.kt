package com.hdw.bookmarker.feature.settings.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.hdw.bookmarker.feature.settings.SettingsViewModel
import com.hdw.bookmarker.feature.settings.appearance.AppearanceViewModel
import com.hdw.bookmarker.feature.settings.behavior.BehaviorViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface SettingsMavericksModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun bindSettingsViewModelFactory(factory: SettingsViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(AppearanceViewModel::class)
    fun bindAppearanceViewModelFactory(factory: AppearanceViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(BehaviorViewModel::class)
    fun bindBehaviorViewModelFactory(factory: BehaviorViewModel.Factory): AssistedViewModelFactory<*, *>
}
