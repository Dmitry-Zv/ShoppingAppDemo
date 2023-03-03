package com.example.shoppingapp.repositories

import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.data.remote.ImageResponse
import com.example.shoppingapp.others.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeShoppingRepository : ShoppingRepository {
    private val shoppingItems = mutableListOf<ShoppingItem>()
    private val observableShoppingItems = MutableStateFlow(shoppingItems)
    private val observableTotalPrice = MutableStateFlow(0F)
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshObservable() {
        observableShoppingItems.value = shoppingItems
        observableTotalPrice.value = getTotalPrice()
    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumOf {
            it.price.toDouble() * it.amount.toDouble()
        }.toFloat()
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshObservable()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshObservable()
    }

    override fun observeAllShoppingItems(): Flow<List<ShoppingItem>> {
        return observableShoppingItems

    }

    override fun observeTotalPrice(): Flow<Float> {
        return observableTotalPrice
    }


    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if (shouldReturnNetworkError) {
            Resource.Error(msg = "Error")
        } else {
            Resource.Success(
                data = ImageResponse(
                    listOf(), 0, 0
                )
            )
        }
    }
}