package com.jamitek.photosapp.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SqliteLocalMediaDb(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), LocalMediaDb {

    companion object {
        private const val TAG = "SqliteLocalMediaDb"
        private const val DB_VERSION = 1
        private const val DB_NAME = "metadb.db"
    }

    private object LocalMediaSchema {
        const val TABLE = "localmedia"

        object Column {
            const val ID = "id"
            const val URI = "uri"
            const val FILESIZE = "filesize"
            const val CHECKSUM = "checksum"
            const val IS_UPLOADED = "isuploaded"
        }
    }

    private val db: SQLiteDatabase by lazy { writableDatabase }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = QueryBuilder(QueryBuilder.QueryType.CREATE_TABLE, LocalMediaSchema.TABLE)
            .addField(
                LocalMediaSchema.Column.ID,
                QueryBuilder.FieldType.INTEGER,
                nullable = false,
                primaryKey = true
            )
            .addField(LocalMediaSchema.Column.URI, QueryBuilder.FieldType.TEXT, nullable = false)
            .addField(LocalMediaSchema.Column.FILESIZE, QueryBuilder.FieldType.INTEGER, nullable = false)
            .addField(LocalMediaSchema.Column.CHECKSUM, QueryBuilder.FieldType.TEXT, nullable = false)
            .addField(LocalMediaSchema.Column.IS_UPLOADED, QueryBuilder.FieldType.INTEGER, nullable = false)
            .build()

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Build something for DB upgrades
    }

    override suspend fun getAll(): ArrayList<LocalMedia> {
        val sql = QueryBuilder(QueryBuilder.QueryType.SELECT_ALL, LocalMediaSchema.TABLE).build()
        val ret = ArrayList<LocalMedia>()
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            ret.add(cursorToLocalMedia(cursor))
        }
        cursor.close()
        return ret
    }

    override suspend fun persist(localMedia: LocalMedia) {
        // If the media already exists, UPDATE existing record.
        // If the media doesn't already exist in the DB, INSERT it.
        if (localMedia.id > 0) {
            val sql = QueryBuilder(QueryBuilder.QueryType.UPDATE, LocalMediaSchema.TABLE)
                .addTextValue(LocalMediaSchema.Column.URI, localMedia.uri)
                .addTextValue(LocalMediaSchema.Column.CHECKSUM, localMedia.checksum)
                .addLongValue(LocalMediaSchema.Column.FILESIZE, localMedia.fileSize)
                .addIntegerValue(LocalMediaSchema.Column.IS_UPLOADED, if (localMedia.uploaded) 1 else 0)
                .addIntegerCondition(LocalMediaSchema.Column.ID, localMedia.id)
                .build()
            safeTransaction { db.execSQL(sql) }
        } else {
            val sql = QueryBuilder(QueryBuilder.QueryType.INSERT, LocalMediaSchema.TABLE)
                .addTextValue(LocalMediaSchema.Column.URI, localMedia.uri)
                .addTextValue(LocalMediaSchema.Column.CHECKSUM, localMedia.checksum)
                .addLongValue(LocalMediaSchema.Column.FILESIZE, localMedia.fileSize)
                .addIntegerValue(LocalMediaSchema.Column.IS_UPLOADED, if (localMedia.uploaded) 1 else 0)
                .build()
            if (safeTransaction { db.execSQL(sql) }) {
                localMedia.id = getLastInsertRowId()
            }
        }

    }

    override suspend fun delete(localMedia: LocalMedia) {
        val sql = QueryBuilder(QueryBuilder.QueryType.DELETE, LocalMediaSchema.TABLE)
            .addIntegerCondition(LocalMediaSchema.Column.ID, localMedia.id)
            .build()
        safeTransaction { db.execSQL(sql) }
    }

    private fun cursorToLocalMedia(cursor: Cursor): LocalMedia {
        val id = cursor.getInt(cursor.getColumnIndex(LocalMediaSchema.Column.ID))
        val uri = cursor.getString(cursor.getColumnIndex(LocalMediaSchema.Column.URI))
        val fileSize = cursor.getLong(cursor.getColumnIndex(LocalMediaSchema.Column.FILESIZE))
        val checksum = cursor.getString(cursor.getColumnIndex(LocalMediaSchema.Column.CHECKSUM))
        val isUploaded = cursor.getInt(cursor.getColumnIndex(LocalMediaSchema.Column.IS_UPLOADED)) == 1

        return LocalMedia(id, uri, fileSize, checksum, isUploaded)
    }

    private fun safeTransaction(block: () -> Unit): Boolean {
        db.beginTransaction()
        var success = false

        try {
            block()
            db.setTransactionSuccessful()
            success = true
        } catch (e: Exception) {
            Log.e(TAG, "DB Error: ", e)
        } finally {
            db.endTransaction()
        }

        return success
    }

    private fun getLastInsertRowId(): Int {
        val cursor = db.rawQuery("select last_insert_rowid()", null)
        cursor.moveToFirst()
        val rowId = cursor.getInt(0)
        cursor.close()
        return rowId
    }

}