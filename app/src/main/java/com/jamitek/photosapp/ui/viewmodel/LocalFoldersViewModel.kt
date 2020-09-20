package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.locallibrary.LocalFoldersRepository
import com.jamitek.photosapp.model.LocalFolder

class LocalFoldersViewModel(private val repo: LocalFoldersRepository) : ViewModel() {

    private val mutableSelectedFolder = MutableLiveData<LocalFolder?>()
    val selectedFolder: LiveData<LocalFolder?> = mutableSelectedFolder

    val localFolders = repo.localFolders
    val scanRunning = repo.scanRunning
    val rootDirSet: Boolean
        get() = repo.localFoldersRootUri != null

    fun initReScan() {
        repo.reScanLocalFolders()
    }

    fun onRootDirChanged(newRootDir: Uri) {
        repo.localFoldersRootUri = newRootDir.toString()
    }

    fun onFolderClicked(folder: LocalFolder) {
        mutableSelectedFolder.value = folder
    }

    fun onFolderOpened() {
        mutableSelectedFolder.value = null
    }

}