package com.jamitek.photosapp

import android.app.Application

class PhotosApplication : Application() {
    companion object {
        lateinit var INSTANCE: PhotosApplication
    }

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }
}