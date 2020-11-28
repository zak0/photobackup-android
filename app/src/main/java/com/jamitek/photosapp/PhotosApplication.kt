package com.jamitek.photosapp

import android.app.Application

class PhotosApplication : Application() {

    lateinit var dependencyRoot: DependencyRoot

    override fun onCreate() {
        super.onCreate()
        dependencyRoot = DependencyRoot(this)
    }
}