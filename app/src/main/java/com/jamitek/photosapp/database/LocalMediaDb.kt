package com.jamitek.photosapp.database

import com.jamitek.photosapp.model.Photo

interface LocalMediaDb {
    suspend fun getAll(): ArrayList<LocalMedia>
    suspend fun persist(localMedia: LocalMedia)
    suspend fun delete(localMedia: LocalMedia)
}