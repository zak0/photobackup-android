package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.viewmodel.LocalLibraryViewModel
import com.jamitek.photosapp.ui.viewmodel.SettingsViewModel
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
                localLibScanStatus.text =
                    "scanning: ${status.isScanning}\nlocalFilesCount: ${status.localFilesCount}\nnotSyncedCount: ${status.waitingForBackupCount}"
            }
        })

        initRemoteLibScanButton.setOnClickListener {
            settingsViewModel.initRemoteLibraryScan()
        }

        refreshRemoteLibScanStatusButton.setOnClickListener {
            settingsViewModel.refreshRemoteLibraryScanStatus()
        }

        settingsViewModel.remoteLibraryScanStatus.observe(viewLifecycleOwner, Observer {
            it?.also { remoteLibraryScanStatus ->
                remoteLibScanStatus.text =
                    """
                        Server scan state: ${remoteLibraryScanStatus.state}
                        mediaFilesDetected: ${remoteLibraryScanStatus.mediaFilesDetected}
                        filesMoved: ${remoteLibraryScanStatus.filesMoved}
                        filesRemoved: ${remoteLibraryScanStatus.filesRemoved}
                        newFiles: ${remoteLibraryScanStatus.newFiles}
                        filesToProcess: ${remoteLibraryScanStatus.filesToProcess}
                        filesProcessed: ${remoteLibraryScanStatus.filesProcessed}
                    """.trimIndent()
            }
        })
    }
}