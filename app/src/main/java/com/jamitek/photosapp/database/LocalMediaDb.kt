package com.jamitek.photosapp.database

import com.jamitek.photosapp.model.LocalMedia

interface LocalMediaDb {
    fun getAll(): ArrayList<LocalMedia>
    fun persist(localMedia: LocalMedia)
    fun delete(localMedia: LocalMedia)
}