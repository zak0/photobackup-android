package com.jamitek.photosapp.networking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosRetrofitService {
    @GET("media")
    fun getAllMedia(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<String>
}
