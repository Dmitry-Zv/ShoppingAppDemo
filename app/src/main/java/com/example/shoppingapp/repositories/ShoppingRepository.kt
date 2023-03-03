package com.example.shoppingapp.repositories

import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.data.remote.ImageResponse
import com.example.shoppingapp.others.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): Flow<List<ShoppingItem>>

    fun observeTotalPrice(): Flow<Float>

    suspend fun searchForImage(imageQuery:String): Resource<ImageResponse>

}