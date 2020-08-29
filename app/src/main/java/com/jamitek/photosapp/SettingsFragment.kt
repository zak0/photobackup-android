package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.locallibrary.LocalLibraryViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
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
            localLibraryViewModel.backup()
        }

        cameraDirLabel.text = "Camera dir: ${settingsViewModel.cameraDirUriString}"

        localLibraryViewModel.libraryStatus.observe(viewLifecycleOwner, Observer {
            it?.also { status ->
                libraryStatusLabel.text =
                    "scanning: ${status.isScanning}\nlocalFilesCount: ${status.localFilesCount}\nnotSyncedCount: ${status.waitingForBackupCount}"
            }
        })
    }
}