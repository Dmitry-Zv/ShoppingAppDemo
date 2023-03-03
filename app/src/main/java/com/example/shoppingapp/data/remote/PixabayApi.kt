package com.example.shoppingapp.data.remote

import com.example.shoppingapp.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    @GET("api/")
    suspend fun searchForImages(
        @Query("q") imageQuery:String,
        @Query("key") apiKey:String = BuildConfig.API_KEY
    ):Response<ImageResponse>

}