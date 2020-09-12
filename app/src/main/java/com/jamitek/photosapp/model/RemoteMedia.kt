package com.jamitek.photosapp.model

data class RemoteMedia(

    /** Only valid for remote photos! ID of this photo on server */
    var serverId: Int,

    /** Name of the file (without the path) */
    var fileName: String,

    /** Size of the file in bytes */
    var fileSize: Long,

    /** Only valid for remote photos! Path to the containing directory in remote server */
    var serverDirPath: String?,

    /** Checksum for the contents of the file */
    var hash: String,

    /** Timestamp for the photo in "EXIF" date format ("2020:07:22 12:59:59") */
    var dateTimeOriginal: String,

    /** Status of the photo */
    var status: String?
) {
    object Status {
        const val READY = "ready"
        const val UNKNOWN = "unknown"
        const val UPLOAD_PENDING = "upload_pending"
        const val PROCESSING = "processing"
    }
}