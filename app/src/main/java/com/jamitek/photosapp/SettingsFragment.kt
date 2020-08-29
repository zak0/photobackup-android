package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.locallibrary.LocalLibraryViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.worker.WorkerService
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val settingsViewModel by lazy { getActivityViewModel(SettingsViewModel::class.java) }
    private val localLibraryViewModel by lazy { getActivityViewModel(LocalLibraryViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCameraDirButton.setOnClickListener {
            StorageAccessHelper.promptRootDirSelection(requireActivity())
        }

        scanCameraDirButton.setOnClickListener {
            localLibraryViewModel.scan()
        }

        manualSyncButton.setOnClickListener {
            localLibraryViewModel.scan()
            WorkerService.start(requireContext())
        }

        cameraDirLabel.text = "Camera dir: ${settingsViewModel.cameraDirUriString}"
    }
}