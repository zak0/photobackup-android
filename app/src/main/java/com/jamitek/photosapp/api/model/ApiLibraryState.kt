package com.jamitek.photosapp.api.model

import com.jamitek.photosapp.serializer.EnumAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable(with = ApiLibraryStateSerializer::class)
enum class ApiLibraryState(val serialName: String) {
    ScanningForFiles("SCANNING_FOR_FILES"),
    ProcessingFiles("PROCESSING_FILES"),
    Done("DONE")
}

@Serializer(forClass = ApiLibraryState::class)
object ApiLibraryStateSerializer :
    EnumAsStringSerializer<ApiLibraryState>("ApiLibraryStateSerializer") {
    override fun values(): Array<ApiLibraryState> = ApiLibraryState.values()
    override fun serialName(forValue: ApiLibraryState): String = forValue.serialName
}
