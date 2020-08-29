package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.worker.WorkerService

class PhotosApplication : Application() {

    lateinit var dependencyRoot: DependencyRoot

    override fun onCreate() {
        super.onCreate()
        dependencyRoot = DependencyRoot(this)
    }
}