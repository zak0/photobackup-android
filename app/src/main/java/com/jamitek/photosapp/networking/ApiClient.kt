package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.database.LocalMedia
import com.jamitek.photosapp.model.Photo
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient(
    private val serializer: PhotosSerializer,
    private val responseParser: ResponseParser
) {

    companion object {
        private const val TAG = "ApiClient"
    }

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

    fun getAllPhotos(callback: (Boolean, List<Photo>) -> Unit) = getAllPhotos(0, callback)

    fun getAllPhotos(offset: Int, callback: (Boolean, List<Photo>) -> Unit) {
        val retroFitCallback = object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val photos =
                        response.body()?.let { responseParser.parsePhotosJson(it.string()) }
                            ?: emptyList()
                    callback(true, photos)
                    Log.d(TAG, "getAllPhotos() - response: 200")
                } else {
                    callback(false, emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Retrieving photos failed: ", t)
                callback(false, emptyList())
            }
        }

        retrofitService.getAllMedia(100000, offset).enqueue(retroFitCallback)
    }

    fun postPhotoMetaData(localMedia: LocalMedia): Int? {
        val body = serializer.getPhotoMetaRequest(localMedia)
        val response = retrofitService.postPhotoMetaData(
            RequestBody.create(
                MediaType.parse("application/json"),
                body
            )
        ).execute()

        var serverId: Int? = null
        if (response.isSuccessful) {
            serverId = response.body()?.string()?.let {
                try {
                    JSONObject(it).getInt("id")
                } catch (e: JSONException) {
                    null
                }
            }
        } else {
            Log.e(TAG, "Posting upload initialization failed")
        }

        return serverId
    }

    fun uploadPhoto(
        serverId: Int,
        localMedia: LocalMedia,
        file: ByteArray
    ): Boolean {
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val body =
            MultipartBody.Part.createFormData("newFile", localMedia.fileName, requestFile)

        val response = retrofitService.uploadPhoto(serverId, body).execute()

        return (response.isSuccessful && response.code() == 201)
    }
}