package com.jamitek.photosapp.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jamitek.photosapp.PhotosApplication
import com.jamitek.photosapp.R

class WorkerService : Service() {

    companion object {
        private const val TAG = "WorkerService"
        private const val NOTIFICATION_ID = 715519
        private const val NOTIFICATION_CHANNEL_ID = "PhotosAppWork"
        private const val NOTIFICATION_CHANNEL_NAME = "Photos Worker"

        fun start(context: Context) {
            val intent = Intent(context, WorkerService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(Intent(context, WorkerService::class.java))
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WorkerService::class.java))
        }
    }

    private val baseNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.appName))
    }

    private val notification: Notification by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        baseNotificationBuilder
            .setContentText("Processing...")
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate()")
        startForeground(NOTIFICATION_ID, notification)

        val dependencyRoot = (application as PhotosApplication).dependencyRoot

        // Scan local folders
        // Upload photos that are not yet backed up

//        GlobalScope.launch {
//            updateNotificationText("Scanning local photos...")
//
//            // Scan local camera directory
//            SharedPrefsPersistence.cameraDirUriString?.also { cameraDirUriString ->
//                Log.d(TAG, "Processing camera dir... 1")
//                StorageAccessHelper.iterateCameraDir(applicationContext, cameraDirUriString)
//                Log.d(TAG, "Processing camera dir... 2")
//            }
//
//            // Get changes from the server
//            Log.d(TAG, "Requesting photos from server... 1")
//            updateNotificationText("Fetching changes from the server...")
//            ApiClient.getAllPhotos { success, photos ->
//                Log.d(TAG, "Requesting photos from server... 2")
//                GlobalScope.launch {
//                    Log.d(TAG, "Requesting photos from server... 3")
//                    if (success) {
//                        RemoteLibraryRepository.onRemotePhotosLoaded(photos)
//                    }
//                    Log.d(TAG, "Requesting photos from server... 4")
//
//                    // Upload photos that are pending to be uploaded
//                    if (success) {
//                        uploadPhotosPendingBackup()
//                    }
//
//                    // Stop the service when done
//                    stop(applicationContext)
//                }
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.also {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            it.createNotificationChannel(channel)
        }
    }

    private fun uploadPhotosPendingBackup() {
//        updateNotificationText("Uploading new photos...")
//
//        val uploadLambda = { photo: Photo ->
//            ApiClient.uploadPhoto(applicationContext, photo) { success ->
//                if (success) {
//                    Log.d(
//                        TAG,
//                        "Upload successful: serverId: ${photo.serverId}, '${photo.fileName}'"
//                    )
//                } else {
//                    Log.e(TAG, "Upload failed: serverId: ${photo.serverId}, '${photo.fileName}'")
//                }
//            }
//        }
//
//        // Get all photos that are only local
//        RemoteLibraryRepository.allPhotos.value?.filter {
//            it.isLocal || (it.isLocal && it.isPendingUpload)
//        }?.forEach { photo ->
//            // If meta data is already uploaded, then just upload the file
//            if (photo.isPendingUpload) {
//                uploadLambda(photo)
//            } else {
//                // First upload meta data, then the file itself
//                ApiClient.postPhotoMetaData(photo) { serverId ->
//                    serverId?.also {
//                        photo.serverId = serverId
//                        Log.d(TAG, "Metadata POST successful, serverId: $serverId, '${photo.fileName}'")
//                        uploadLambda(photo)
//                    } ?: run {
//                        Log.d(TAG, "Metadata POST failed, '${photo.fileName}'")
//                    }
//                }
//            }
//        }
    }

    private fun updateNotificationText(newText: String) {
        val newNotification = baseNotificationBuilder
            .setContentText(newText)
            .build()

        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.also {
            it.notify(NOTIFICATION_ID, newNotification)
        }
    }
}
