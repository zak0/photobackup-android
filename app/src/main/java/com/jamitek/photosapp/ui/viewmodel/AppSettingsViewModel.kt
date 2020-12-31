package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.appsettings.AppSettingsSettingItemKey
import com.jamitek.photosapp.appsettings.AppSettingsUseCase

class AppSettingsViewModel(private val useCase: AppSettingsUseCase) : ViewModel() {

    val uiEvent = useCase.uiEvent
    val items = useCase.settingItems

    fun onItemClicked(key: AppSettingsSettingItemKey) {
        useCase.onItemClicked(key)
    }

    fun onItemToggled(key: AppSettingsSettingItemKey, isChecked: Boolean) {
        useCase.onItemToggled(key, isChecked)
    }

}
