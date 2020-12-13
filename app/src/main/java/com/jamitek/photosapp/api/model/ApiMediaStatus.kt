package com.jamitek.photosapp.api.model

import com.jamitek.photosapp.serializer.EnumAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable(with = ApiMediaStatusSerializer::class)
enum class ApiMediaStatus(val serialName: String) {
    UploadPending("upload_pending"),
    Processing("processing"),
    Ready("ready"),
    NotOnServer("")
}

@Serializer(forClass = ApiMediaStatus::class)
object ApiMediaStatusSerializer :
    EnumAsStringSerializer<ApiMediaStatus>("ApiMediaStatusSerializer") {
    override fun values(): Array<ApiMediaStatus> = ApiMediaStatus.values()
    override fun serialName(forValue: ApiMediaStatus): String = forValue.serialName
}