package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.locallibrary.LocalFoldersRepository

class LocalFoldersViewModel(private val repo: LocalFoldersRepository) : ViewModel() {

    val localFolders = repo.localFolders
    val scanRunning = repo.scanRunning
    val rootDirSet: Boolean
        get() = repo.localFoldersRootUri != null

    fun initScan() {
        repo.scan()
    }

    fun onRootDirChanged(newRootDir: Uri) {
        repo.localFoldersRootUri = newRootDir.toString()
    }

}