package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.viewmodel.LocalCameraViewModel
import com.jamitek.photosapp.ui.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val settingsViewModel by lazy { getActivityViewModel(SettingsViewModel::class.java) }
    private val localLibraryViewModel by lazy { getActivityViewModel(LocalCameraViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCameraDirButton.setOnClickListener {
            StorageAccessHelper.promptCameraDirSelection(requireActivity())
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
                    """
                        uploading: ${status.isUploading}
                        scanning: ${status.isScanning}
                        localFilesCount: ${status.localFilesCount}
                        notSyncedCount: ${status.waitingForBackupCount}
                    """.trimIndent()
            }
        })

        initRemoteLibScanButton.setOnClickListener {
            settingsViewModel.initRemoteLibraryScan()
        }

        refreshRemoteLibScanStatusButton.setOnClickListener {
            settingsViewModel.refreshRemoteLibraryScanStatus()
        }

        setCredentialsButton.setOnClickListener {
            settingsViewModel.setCredentials(
                userName.text.toString(),
                password.text.toString()
            )
        }

        setLocalFoldersRootButton.setOnClickListener {
            StorageAccessHelper.promptLocalFoldersRootDirSelection(requireActivity())
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

        setupServerUrlViews()
    }

    private fun setupServerUrlViews() {
        if (settingsViewModel.serverAddressIsSet) {
            currentServerUrl.text = settingsViewModel.currentServerAddress
        } else {
            currentServerUrl.setText(R.string.errorServerUrlNotSet)
        }

        addServerButton.setOnClickListener {
            settingsViewModel.addServer(serverUrlEdit.text.toString())
            setupServerUrlViews()
        }

        clearAllServerUrls.setOnClickListener {
            settingsViewModel.clearServers()
            setupServerUrlViews()
        }
    }
}