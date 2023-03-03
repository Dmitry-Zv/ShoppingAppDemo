package com.example.shoppingapp.repositories

import com.example.shoppingapp.data.local.ShoppingDao
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.data.remote.ImageResponse
import com.example.shoppingapp.data.remote.PixabayApi
import com.example.shoppingapp.others.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao,
    private val pixabayApi: PixabayApi
) : ShoppingRepository {
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        dao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        dao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): Flow<List<ShoppingItem>> =
        dao.observeAllShoppingItems()


    override fun observeTotalPrice(): Flow<Float> = dao.observeTotalPrice()

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try {
            val response = pixabayApi.searchForImages(imageQuery = imageQuery)
            if (response.isSuccessful) response.body()?.let {
                return@let Resource.Success(data = it)
            }?: Resource.Error("An unknown error")
            else {
                Resource.Error(msg = "An unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(msg = "Couldn't reach the server, check your internet connection")
        }
    }
}