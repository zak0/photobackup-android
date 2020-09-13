package com.jamitek.photosapp.networking

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PhotosRetrofitService {
    @GET("media")
    fun getAllMedia(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<ResponseBody>

    @POST("media")
    fun postPhotoMetaData(@Body body: RequestBody): Call<ResponseBody>

    @Multipart
    @POST("media/{id}/file")
    fun uploadPhoto(@Path(value = "id") serverId: Int, @Part file: MultipartBody.Part): Call<ResponseBody>

    @GET("scanlibrary")
    fun initRemoteLibraryScan(): Call<ResponseBody>

    @GET("scanstatus")
    fun getRemoteLibraryScanStatus(): Call<ResponseBody>
}
