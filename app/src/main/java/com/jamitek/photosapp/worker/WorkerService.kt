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
import com.jamitek.photosapp.R
import com.jamitek.photosapp.Repository
import com.jamitek.photosapp.database.SharedPrefsPersistence
import com.jamitek.photosapp.networking.ApiClient
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        GlobalScope.launch {
            updateNotificationText("Scanning local photos...")

            // Scan local camera directory
            SharedPrefsPersistence.cameraDirUriString?.also { cameraDirUriString ->
                Log.d(TAG, "Processing camera dir... 1")
                StorageAccessHelper.iterateCameraDir(applicationContext, cameraDirUriString)
                Log.d(TAG, "Processing camera dir... 2")
            }

            // Get changes from the server
            Log.d(TAG, "Requesting photos from server... 1")
            updateNotificationText("Fetching changes from the server...")
            ApiClient.getAllPhotos { success, photos ->
                Log.d(TAG, "Requesting photos from server... 2")
                GlobalScope.launch {
                    Log.d(TAG, "Requesting photos from server... 3")
                    if (success) {
                        Repository.onRemotePhotosLoaded(photos)
                    }
                    Log.d(TAG, "Requesting photos from server... 4")

                    // TODO Upload local photos that don't exist on server

                    // Stop the service when done
                    stop(applicationContext)
                }
            }
        }
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

    private fun getSyncServiceStatusString(
        localPhotosDone: Boolean,
        remotePhotosDone: Boolean,
        uploadDone: Boolean
    ): String {
        return StringBuilder()
            .append(if (localPhotosDone) "✔ " else "")
            .append("Scan local photos")
            .append("\n")
            .append(if (remotePhotosDone) "✔ " else "")
            .append("Fetch photos from server")
            .append("\n")
            .append(if (uploadDone) "✔ " else "")
            .append("Backup new photos")
            .toString()
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
