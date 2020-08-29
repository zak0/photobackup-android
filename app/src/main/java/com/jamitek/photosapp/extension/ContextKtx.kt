package com.jamitek.photosapp.extension

import android.content.Context
import com.jamitek.photosapp.DependencyRoot
import com.jamitek.photosapp.PhotosApplication

val Context.dependencyRoot: DependencyRoot
    get() = (applicationContext as PhotosApplication).dependencyRoot
