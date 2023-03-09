package com.example.shoppingapp.data.remote

import com.example.shoppingapp.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    @GET("api/")
    suspend fun searchForImages(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("q") imageQuery: String
    ): Response<ImageResponse>

}