package com.jamitek.photosapp.networking

import retrofit2.Call
import retrofit2.http.GET

interface PhotosRetrofitService {
    @GET("media")
    fun getAllMedia(): Call<String>
}
