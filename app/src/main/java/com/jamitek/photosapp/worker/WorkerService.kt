package com.jamitek.photosapp.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import com.jamitek.photosapp.PhotosApplication
import com.jamitek.photosapp.R

/**
 * Background scanning of local camera directory and uploading of files that are not yet backed up.
 */
class WorkerService : LifecycleService() {

    companion object {
        private const val TAG = "WorkerService"

        /**
         * ID of the notification that is bound to the foreground [Service].
         */
        private const val BOUND_NOTIFICATION_ID = 715519
        private const val BOUND_NOTIFICATION_CHANNEL_ID = "PhotosAppWork"
        private const val BOUND_NOTIFICATION_CHANNEL_NAME = "Photos Worker"

        /**
         * ID of the notification to report the result of the backup job.
         * This is not bound to the [Service] and is meant to stay until
         * user dismisses it.
         */
        private const val NOTIFICATION_ID = 715518
        private const val NOTIFICATION_CHANNEL_ID = "PhotosApp"
        private const val NOTIFICATION_CHANNEL_NAME = "Photos Worker"

        fun start(context: Context) {
            val intent = Intent(context, WorkerService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WorkerService::class.java))
        }
    }

    private val baseNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(this, BOUND_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Backing up camera")
    }

    private val boundNotification: Notification by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }

        baseNotificationBuilder
            .setContentText("Processing...")
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate()")
        startForeground(BOUND_NOTIFICATION_ID, boundNotification)

        val dependencyRoot = (application as PhotosApplication).dependencyRoot
        val useCase = dependencyRoot.backgroundBackupUseCase

        useCase.scanAndBackup()

        useCase.workStatus.observe(this) {
            when (it) {
                WorkStatus.Scanning -> updateBoundNotificationText("Scanning camera directory...")
                WorkStatus.Uploading -> updateBoundNotificationText("Uploading new files...")
                WorkStatus.Done -> {
                    showNotification(useCase.completionNotificationMessage)
                    stop(applicationContext)
                }
                WorkStatus.Unknown,
                WorkStatus.Idle -> Unit
            }
        }
    }

    private fun createNotificationChannels() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.also {
            val boundNotificationChannel = NotificationChannel(
                BOUND_NOTIFICATION_CHANNEL_ID,
                BOUND_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            val normalChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            it.createNotificationChannel(boundNotificationChannel)
            it.createNotificationChannel(normalChannel)
        }
    }

    private fun updateBoundNotificationText(newText: String) {
        baseNotificationBuilder
            .setContentText(newText)
            .build()
            .show(BOUND_NOTIFICATION_ID)
    }

    private fun showNotification(message: String) {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Backup complete")
            .setContentText(message)
            .build()
            .show(NOTIFICATION_ID)
    }

    private fun Notification.show(id: Int) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.also {
            it.notify(id, this)
        }
    }
}
