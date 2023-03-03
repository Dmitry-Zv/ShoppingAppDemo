package com.example.shoppingapp.di

import android.content.Context
import androidx.room.Room
import com.example.shoppingapp.data.local.ShoppingDao
import com.example.shoppingapp.data.local.ShoppingDatabase
import com.example.shoppingapp.data.remote.PixabayApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideShoppingDatabase(@ApplicationContext context: Context): ShoppingDatabase =
        Room.databaseBuilder(context, ShoppingDatabase::class.java, "shopping_item_table")
            .build()

    @Provides
    @Singleton
    fun provideShoppingDao(database: ShoppingDatabase): ShoppingDao = database.shoppingDao()

    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://pixabay.com/"

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePixabayApi(retrofit: Retrofit): PixabayApi =
        retrofit.create(PixabayApi::class.java)


}