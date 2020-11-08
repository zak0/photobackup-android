package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.backup.BackupRepository
import com.jamitek.photosapp.locallibrary.LocalCameraRepository

class BackupViewModel(private val repo: BackupRepository) : ViewModel() {

    val items = repo.settingItems

}
