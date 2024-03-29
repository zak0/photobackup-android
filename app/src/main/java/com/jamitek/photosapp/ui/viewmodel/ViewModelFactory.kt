package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamitek.photosapp.DependencyRoot

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dependencyRoot: DependencyRoot) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            RootViewModel::class.java -> RootViewModel(
                dependencyRoot.setCameraDirUseCase
            )
            MediaTimelineViewModel::class.java -> MediaTimelineViewModel(
                dependencyRoot.mediaTimelineUseCase
            )
            AppSettingsViewModel::class.java -> AppSettingsViewModel(
                dependencyRoot.appSettingsUseCase
            )
            ServerSetupViewModel::class.java -> ServerSetupViewModel(dependencyRoot.serverConfigUseCase)

            TimelineSettingsViewModel::class.java -> TimelineSettingsViewModel()

            else -> throw IllegalArgumentException("Unknown ViewModel type")
        } as T
    }
}