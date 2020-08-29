package com.jamitek.photosapp.database

interface LocalMediaDb {
    fun getAll(): ArrayList<LocalMedia>
    fun persist(localMedia: LocalMedia)
    fun delete(localMedia: LocalMedia)
}