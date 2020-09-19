package com.jamitek.photosapp.locallibrary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.LocalMediaDb
import com.jamitek.photosapp.model.LocalFolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocalFoldersRepository(
    private val keyValueStore: KeyValueStore,
    private val scanner: LocalLibraryScanner,
    private val db: LocalMediaDb
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

    var localFoldersRootUri: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_LOCAL_FOLDERS_ROOT_URI)
        set(value) {
            keyValueStore.putString(KeyValueStore.KEY_LOCAL_FOLDERS_ROOT_URI, value)
        }

    private var scanJob: Job? = null

    fun scan() {
        // Only allow one scan to run at a time
        if (scanJob?.isActive == true) {
            Log.d(TAG, "Scan already running. Not starting a new one...")
            return
        }

        localFoldersRootUri?.also { rootUri ->
            mutableScanRunning.value = true
            scanJob = CoroutineScope(Dispatchers.IO).launch {
                scanner.iterateLocalFolders(rootUri) { media, folderDocFile ->

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

                    db.persist(media)

                    Log.d(
                        TAG,
                        "filename: ${media.fileName}, folder: ${folder.name}"
                    )
                }

                // Expose folders in a way that is consumable by the UI
                val newLocalFolders = localFoldersByUri.entries.map { it.value }.sortedBy { it.name }

                CoroutineScope(Dispatchers.Main).launch {
                    mutableLocalFolders.value = newLocalFolders
                    mutableScanRunning.value = false
                }
            }
        }
    }

}