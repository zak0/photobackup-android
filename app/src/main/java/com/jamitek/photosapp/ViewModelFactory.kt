package com.jamitek.photosapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dependencyRoot: DependencyRoot) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LocalLibraryViewModel::class.java -> LocalLibraryViewModel()
            RemoteLibraryViewModel::class.java -> RemoteLibraryViewModel(dependencyRoot.remoteLibraryRepository)
            SettingsViewModel::class.java -> SettingsViewModel(dependencyRoot.keyValueStore)
            else -> throw IllegalArgumentException("Unknown ViewModel type")
        } as T
    }
}