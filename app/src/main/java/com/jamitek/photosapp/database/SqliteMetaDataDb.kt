package com.jamitek.photosapp.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.jamitek.photosapp.model.Photo
import java.sql.SQLException

class SqliteMetaDataDb(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), MetaDataDb {

    companion object {
        private const val TAG = "SqliteMetaDataDb"
        private const val DB_VERSION = 1
        private const val DB_NAME = "metadb.db"
    }

    private val db: SQLiteDatabase by lazy { writableDatabase }

    override fun onCreate(db: SQLiteDatabase) {
        val createPhotoTable = """
            CREATE TABLE IF NOT EXISTS "photo" (
            "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            "serverid" INTEGER,
            "filename" TEXT,
            "filesize" INTEGER,
            "hash" TEXT,
            "status" TEXT DEFAULT "unknown",
            "datetimeoriginal" TEXT,
            "serverdirpath" TEXT,
            "localimageuri" TEXT,
            "localthumbnailuri" TEXT)
            """

        db.execSQL(createPhotoTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Build something for DB upgrades
    }

    override suspend fun getAllPhotos(): ArrayList<Photo> {
        val sql = """
            SELECT
             "id",
             "serverid",
             "filename",
             "filesize",
             "hash",
             "status",
             "datetimeoriginal",
             "serverdirpath",
             "localimageuri",
             "localthumbnailuri"
             FROM "photo" ORDER BY "datetimeoriginal" DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val dbPhotos = ArrayList<Photo>()
        while (!cursor.isAfterLast) {
            dbPhotos.add(cursorToPhoto(cursor))
            cursor.moveToNext()
        }
        cursor.close()
        return dbPhotos
    }

    override suspend fun persistPhoto(photo: Photo) {
        writableDatabase.beginTransaction()

        try {
            if (photo.id ?: -1 >= 0) {
                Log.d(TAG, "persistPhoto() - UPDATE id: ${photo.id}, filename: ${photo.fileName}")
                // Localphoto entry already exists, update existing
                val sql = """
                       UPDATE "photo" SET
                        "serverid" = ${photo.serverId}
                        "filename" = "${photo.fileName}",
                        "filesize" = ${photo.fileSize},
                        "hash" = "${photo.hash}",
                        "status = "${photo.status}",
                        "serverdirpath" = "${photo.serverDirPath}",
                        "datetimeoriginal" = "${photo.dateTimeOriginal}"
                        WHERE "id" = ${photo.id}
                    """.trimIndent()
                db.execSQL(sql)
            } else {
                Log.d(TAG, "persistPhoto() - INSERT id: ${photo.id}, filename: ${photo.fileName}")
                // It doesn't exist in the database yet, let's insert
                val sql = """
                INSERT INTO "photo" (
                "serverid",
                "filename",
                "filesize",
                "hash",
                "status",
                "serverdirpath",
                "datetimeoriginal") VALUES (
                ${photo.serverId},
                "${photo.fileName}",
                ${photo.fileSize},
                "${photo.hash}",
                "${photo.status}",
                "${photo.serverDirPath}",
                "${photo.dateTimeOriginal}"
                )
                """.trimIndent()
                db.execSQL(sql)

                // Get ID of the last insertion and store that to the Photo object
                photo.id = getLastInsertRowId()
            }

            writableDatabase.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "persistPhoto() - failed: ", e)
        } finally {
            writableDatabase.endTransaction()
        }

    }

    override suspend fun deletePhoto(photo: Photo) {
    }

    private fun getLastInsertRowId(): Int {
        val cursor = db.rawQuery("select last_insert_rowid()", null)
        cursor.moveToFirst()
        val rowId = cursor.getInt(0)
        cursor.close()
        return rowId
    }

    private fun cursorToPhoto(cursor: Cursor): Photo {
        val id = cursor.getInt(cursor.getColumnIndex("id"))
        val fileName = cursor.getString(cursor.getColumnIndex("filename"))
        val fileSize = cursor.getLong(cursor.getColumnIndex("filesize"))
        val hash = cursor.getString(cursor.getColumnIndex("hash"))
        val status = cursor.getString(cursor.getColumnIndex("status"))
        val dateTimeOriginal = cursor.getString(cursor.getColumnIndex("datetimeoriginal"))

        val serverId = cursor.getIntOrNull(cursor.getColumnIndex("serverid"))
        val serverDirPath = cursor.getStringOrNull(cursor.getColumnIndex("serverdirpath"))

        val localImageUri = cursor.getStringOrNull(cursor.getColumnIndex("localimageuri"))
        val localThumbnailUri = cursor.getStringOrNull(cursor.getColumnIndex("localthumbnailuri"))

        return Photo(
            id,
            serverId,
            fileName,
            fileSize,
            serverDirPath,
            localImageUri,
            localThumbnailUri,
            hash,
            dateTimeOriginal,
            status
        )
    }
}