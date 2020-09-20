package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamitek.photosapp.DependencyRoot

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dependencyRoot: DependencyRoot) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LocalCameraViewModel::class.java -> LocalCameraViewModel(
                dependencyRoot.localCameraRepository
            )
            RemoteLibraryViewModel::class.java -> RemoteLibraryViewModel(
                dependencyRoot.serverConfigRepository,
                dependencyRoot.remoteLibraryRepository
            )
            SettingsViewModel::class.java -> SettingsViewModel(
                dependencyRoot.keyValueStore,
                dependencyRoot.remoteLibraryAdminRepository,
                dependencyRoot.serverConfigRepository
            )
            LocalFoldersViewModel::class.java -> LocalFoldersViewModel(
                dependencyRoot.localFoldersRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel type")
        } as T
    }
}