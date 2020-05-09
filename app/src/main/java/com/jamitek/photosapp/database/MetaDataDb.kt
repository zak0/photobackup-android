package com.jamitek.photosapp.database

import com.jamitek.photosapp.model.Photo

interface MetaDataDb {
    suspend fun getAllPhotos(): ArrayList<Photo>
    suspend fun persistPhoto(photo: Photo)
    suspend fun deletePhoto(photo: Photo)
}