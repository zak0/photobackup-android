package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.model.Photo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {

    private const val TAG = "ApiClient"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Basic amFha2tvYWRtaW46U2FsYWluZW5TYW5hMTMyNCFA")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(UrlHelper.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val retrofitService = retrofit.create(PhotosRetrofitService::class.java)

    fun getAllPhotos(callback: (List<Photo>) -> Unit) = getAllPhotos(0, callback)

    fun getAllPhotos(offset: Int, callback: (List<Photo>) -> Unit) {
        val retroFitCallback = object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    val photos =
                        response.body()?.let { ResponseParser.parsePhotosJson(it) } ?: emptyList()
                    callback(photos)
                    Log.d(TAG, "getAllPhotos() - response: 200")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Retrieving photos failed: ", t)
            }
        }

        retrofitService.getAllMedia(100000, offset).enqueue(retroFitCallback)
    }
}