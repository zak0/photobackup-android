package com.jamitek.photosapp.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.jamitek.photosapp.PhotosApplication
import com.jamitek.photosapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Background scanning of local camera directory and uploading of files that are not yet backed up.
 */
class BackupWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {

    companion object {
        private const val TAG = "WorkerService"

        /**
         * ID of the notification that is bound to the [Worker].
         */
        private const val BOUND_NOTIFICATION_ID = 715519
        private const val BOUND_NOTIFICATION_CHANNEL_ID = "PhotosAppWork"
        private const val BOUND_NOTIFICATION_CHANNEL_NAME = "Photos Worker"

        /**
         * ID of the notification to report the result of the backup job.
         * This is not bound to the [Worker] and is meant to stay until
         * user dismisses it.
         */
        private const val NOTIFICATION_ID = 715518
        private const val NOTIFICATION_CHANNEL_ID = "PhotosApp"
        private const val NOTIFICATION_CHANNEL_NAME = "Photos Worker"

        fun startNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<BackupWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }

        fun scheduleRecurring(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .build()

            val request = PeriodicWorkRequestBuilder<BackupWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, request)

            Log.d(TAG, "Scheduled background backup")
        }
    }

    private val baseNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(applicationContext, BOUND_NOTIFICATION_CHANNEL_ID)
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

    private val notificationManager: NotificationManager? by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun doWork(): Result {
        val backupDone = AtomicBoolean(false)
        val dependencyRoot = (applicationContext as PhotosApplication).dependencyRoot
        val useCase = dependencyRoot.backgroundBackupUseCase

        // We need to switch to a coroutine context in order to be able to await the
        // binding to a FG service. See KDoc of setForegroundAsync.
        GlobalScope.launch {
            setForegroundAsync(ForegroundInfo(BOUND_NOTIFICATION_ID, boundNotification)).await()

            // Observing a LiveData has to happen in the main thread...
            withContext(Dispatchers.Main) {
                useCase.scanAndBackup()
                useCase.workStatus.observeForever {
                    when (it) {
                        WorkStatus.Scanning -> updateBoundNotificationText("Scanning camera directory...")
                        WorkStatus.Uploading -> updateBoundNotificationText("Uploading new files...")
                        WorkStatus.Done -> {
                            showNotification(useCase.completionNotificationMessage)
                            backupDone.set(true)
                        }
                        WorkStatus.Unknown,
                        WorkStatus.Idle,
                        null -> Unit
                    }
                }
            }
        }

        while(!backupDone.get()) {
            Thread.sleep(1)
        }

        return Result.success()
    }

    private fun createNotificationChannels() {
        notificationManager?.also {
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
        NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Backup complete")
            .setContentText(message)
            .build()
            .show(NOTIFICATION_ID)
    }

    private fun Notification.show(id: Int) {
        notificationManager?.notify(id, this)
    }
}
