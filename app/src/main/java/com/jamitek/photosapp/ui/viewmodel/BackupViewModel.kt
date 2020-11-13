package com.jamitek.photosapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.backup.BackupSettingItemKey
import com.jamitek.photosapp.backup.BackupUseCase

class BackupViewModel(private val useCase: BackupUseCase) : ViewModel() {

    val items = useCase.settingItems

    fun onItemClicked(key: BackupSettingItemKey, activityContext: Context) {
        useCase.onItemClicked(key, activityContext)
    }

}
