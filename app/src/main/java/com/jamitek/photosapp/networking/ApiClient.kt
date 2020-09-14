package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.model.RemoteLibraryScanStatus
import com.jamitek.photosapp.model.RemoteMedia
import okhttp3.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient(
    private val urlRepo: UrlRepository,
    private val serializer: MediaSerializer
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

    private lateinit var retrofitService: PhotosRetrofitService

    init {
        buildRetrofit()

        // Register to rebuild Retrofit service if/when server URL is changed
        urlRepo.registerForSelectedUrlChanges(TAG) { buildRetrofit() }
    }

    private fun buildRetrofit() {
        // Only init Retrofit if a server URL is set. This is likely not the case for example at
        // first startup.
        //
        // If the ApiClient is attempted to be used before Retrofit initialization, crash will
        // ensue.
        if (urlRepo.urlIsSet) {
            val retrofit = Retrofit.Builder()
                .baseUrl(urlRepo.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            retrofitService = retrofit.create(PhotosRetrofitService::class.java)
        }
    }

    fun getAllMedia(callback: (Boolean, List<RemoteMedia>) -> Unit) = getAllMedia(0, callback)

    fun getAllMedia(offset: Int, callback: (Boolean, List<RemoteMedia>) -> Unit) {
        val retroFitCallback = object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val photos =
                        response.body()?.let { serializer.parseRemoteMediasJson(it.string()) }
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

    fun postMetaData(localMedia: LocalMedia): ApiResponse<RemoteMedia> {
        val body = serializer.localMediaToMetaDataPostBody(localMedia)

        try {
            val response = retrofitService.postPhotoMetaData(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    body
                )
            ).execute()


            var remoteMedia: RemoteMedia? = null
            if (response.isSuccessful) {
                remoteMedia = response.body()?.string()?.let {
                    try {
                        serializer.parseRemoteMediaJson(it)
                    } catch (e: JSONException) {
                        null
                    }
                }
            } else {
                Log.e(TAG, "POSTing media meta failed")
            }

            return ApiResponse(response.code(), remoteMedia)
        } catch (e: Exception) {
            Log.e(TAG, "POSTing media meda failed: ", e)
            return ApiResponse(null, null)
        }
    }

    fun uploadMedia(
        serverId: Int,
        localMedia: LocalMedia,
        file: ByteArray
    ): ApiResponse<Boolean> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("newFile", localMedia.fileName, requestFile)

        return try {
            val response = retrofitService.uploadPhoto(serverId, body).execute()
            ApiResponse(response.code(), response.isSuccessful)
        } catch (e: Exception) {
            Log.e(TAG, "POSTing media failed: ", e)
            ApiResponse(null, false)
        }
    }

    /**
     * Requires ADMIN account.
     *
     * Initializes a library scan at the server. Expects server to
     * - Respond with 200 and empty body if a scan is started.
     * - Respond with 409 if there already is a scan running.
     */
    fun initRemoteLibraryScan(): ApiResponse<Boolean> {
        return try {
            val response = retrofitService.initRemoteLibraryScan().execute()
            ApiResponse(response.code(), response.isSuccessful)
        } catch (e: Exception) {
            Log.e(TAG, "Library scan initiation failed: ", e)
            ApiResponse(null, false)
        }
    }

    /**
     * Requires ADMIN account.
     *
     * Fetches status of (possible) current library scan on the server. Expects server to
     * - Respond with 200 and valid scan status body, if there has been a scan this server runtime.
     * - Respond with 409 when there has NOT been a scan this server runtime. In this case the
     *   response body will be null/empty.
     */
    fun getRemoteLibraryScanStatus(): ApiResponse<RemoteLibraryScanStatus> {
        return try {
            val response = retrofitService.getRemoteLibraryScanStatus().execute()
            val statusObject = if (response.isSuccessful) {
                response.body()?.string()?.let { bodyString ->
                    serializer.parseRemoteLibraryScanStatusJson(bodyString)
                }
            } else {
                null
            }
            ApiResponse(response.code(), statusObject)
        } catch (e: Exception) {
            Log.e(TAG, "Library scan status fetching failed: ", e)
            ApiResponse(null, null)
        }
    }
}