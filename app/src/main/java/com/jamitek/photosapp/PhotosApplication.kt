package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.worker.WorkerService

class PhotosApplication : Application() {
    companion object {
        lateinit var INSTANCE: PhotosApplication
    }

    override fun onCreate() {
        INSTANCE = this
        Repository.init(this)
        super.onCreate()

        WorkerService.start(this)
    }
}