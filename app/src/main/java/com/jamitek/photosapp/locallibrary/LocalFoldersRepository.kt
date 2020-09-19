package com.jamitek.photosapp.locallibrary

import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.LocalMediaDb
import com.jamitek.photosapp.model.LocalFolder
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocalFoldersRepository(
    private val keyValueStore: KeyValueStore,
    private val scanner: LocalLibraryScanner,
    private val db: LocalMediaDb,
    private val storageHelper: StorageAccessHelper
) {

    companion object {
        private const val TAG = "LocalFoldersRepository"
    }

    private val localFoldersByUri = HashMap<String, LocalFolder>()

    private val mutableLocalFolders =
        MutableLiveData<List<LocalFolder>>().apply { value = emptyList() }
    val localFolders: LiveData<List<LocalFolder>> = mutableLocalFolders

    private val mutableScanRunning = MutableLiveData<Boolean>().apply { value = false }
    val scanRunning: LiveData<Boolean> = mutableScanRunning

    private val mutableIsInitializing = MutableLiveData<Boolean>().apply { value = true }
    val isInitializing: LiveData<Boolean> = mutableIsInitializing

    var localFoldersRootUri: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_LOCAL_FOLDERS_ROOT_URI)
        set(value) {
            keyValueStore.putString(KeyValueStore.KEY_LOCAL_FOLDERS_ROOT_URI, value)
        }

    private var initJob: Job? = null
    private var scanJob: Job? = null

    init {
        // Initialize by loading folders and contents from cache DB
        mutableIsInitializing.value = true
        initJob = CoroutineScope(Dispatchers.IO).launch {
            storageHelper.treeUriStringToDocFileUriString(localFoldersRootUri)?.let {
                db.getAll()
            }?.also { allMedia ->
                // We're here if loading media meta data from the database succeeded.
                // Let's organize media into folders.
                allMedia.forEach { media ->
                    storageHelper.uriToDocFile(media.directoryUri)?.also {
                        mediaFileToInMemoryCache(media, it)
                    }
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                mutableIsInitializing.value = true
            }

            publishFolders()
        }
    }

    fun scan() {
        // Initialization needs to be complete
        if (initJob?.isActive == true) {
            Log.d(TAG, "Local folders repo not initialized yet. Not starting scan...")
            return
        }

        // Only allow one scan to run at a time
        if (scanJob?.isActive == true) {
            Log.d(TAG, "Scan already running. Not starting a new one...")
            return
        }

        localFoldersRootUri?.also { rootUri ->
            mutableScanRunning.value = true
            scanJob = CoroutineScope(Dispatchers.IO).launch {
                scanner.iterateLocalFolders(rootUri) { media, folderDocFile ->
                    mediaFileToInMemoryCache(media, folderDocFile)
                    db.persist(media)
                }

                publishFolders()
            }

            CoroutineScope(Dispatchers.Main).launch {
                mutableScanRunning.value = false
            }
        }
    }

    /**
     * Exposes folders in a way that is consumable by the UI.
     */
    private fun publishFolders() {
        val newLocalFolders = localFoldersByUri.entries.map { it.value }.sortedBy { it.name }

        CoroutineScope(Dispatchers.Main).launch {
            mutableLocalFolders.value = newLocalFolders
        }
    }

    private fun mediaFileToInMemoryCache(media: LocalMedia, folderDocFile: DocumentFile) {
        val folderUriString = folderDocFile.uri.toString()
        val folder = localFoldersByUri[folderUriString] ?: LocalFolder(
            folderDocFile.name ?: "N/A",
            folderUriString,
            ArrayList()
        ).also {
            // Folder didn't exist yet in memory, let's add it to the maps.
            localFoldersByUri[folderUriString] = it
        }

        folder.media.add(media)
    }

}