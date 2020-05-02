package com.jamitek.photosapp.networking

import android.util.Log
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
        .baseUrl("http://192.168.1.105:3000/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val retrofitService = retrofit.create(PhotosRetrofitService::class.java)

    fun getAllPhotos(callback: (List<Int>) -> Unit) {
        val callback = object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    val photosJson = response.body()?.let { ResponseParser.parsePhotosJson(it) }
                    photosJson?.getJSONArray("files")?.let {
                        val photoIds = ArrayList<Int>()
                        for (i in 0 until it.length()) {
                            photoIds.add(it.getJSONObject(i).getInt("id"))
                        }
                        callback(photoIds)
                    }
                    val foo = "bar"
                    Log.d(TAG, "getAllPhotos() - response: 200")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                val foo = "bar"
            }
        }

        retrofitService.getAllMedia().enqueue(callback)
    }
}