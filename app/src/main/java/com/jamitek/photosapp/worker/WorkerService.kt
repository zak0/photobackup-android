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
        private const val NOTIFICATION_ID = 715519
        private const val NOTIFICATION_CHANNEL_ID = "PhotosAppWork"
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

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate()")
        startForeground(NOTIFICATION_ID, notification)

        val dependencyRoot = (application as PhotosApplication).dependencyRoot
        val useCase = dependencyRoot.backgroundBackupUseCase

        useCase.scanAndBackup()

        useCase.workStatus.observe(this) {
            when (it) {
                WorkStatus.Scanning -> updateNotificationText("Scanning camera directory...")
                WorkStatus.Uploading -> updateNotificationText("Uploading new files...")
                WorkStatus.Done -> {
                    // TODO Display notification of a completed job
                    stop(applicationContext)
                }
                WorkStatus.Unknown,
                WorkStatus.Idle -> {
                    // TODO Display notification of an error
                    stop(applicationContext)
                }
            }
        }
    }

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

    private fun updateNotificationText(newText: String) {
        val newNotification = baseNotificationBuilder
            .setContentText(newText)
            .build()

        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.also {
            it.notify(NOTIFICATION_ID, newNotification)
        }
    }
}
