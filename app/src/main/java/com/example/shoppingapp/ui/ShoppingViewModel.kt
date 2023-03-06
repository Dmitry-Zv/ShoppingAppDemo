package com.example.shoppingapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.data.remote.ImageResponse
import com.example.shoppingapp.others.Constants
import com.example.shoppingapp.others.Resource
import com.example.shoppingapp.repositories.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {

    val shoppingItems = repository.observeAllShoppingItems()
    val totalPrice = repository.observeTotalPrice()

    private val _images = MutableStateFlow<Resource<ImageResponse>>(Resource.Initial)
    val images = _images.asStateFlow()

    private val _curImageUrl = MutableStateFlow("")
    val curImageUrl = _curImageUrl.asStateFlow()

    private val _insertShoppingItemStatus =
        MutableStateFlow<Resource<ShoppingItem>>(Resource.Initial)
    val insertShoppingItemStatus = _insertShoppingItemStatus.asStateFlow()


    fun setCurImageUrl(imageUrl: String) {
        _curImageUrl.value = imageUrl
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun searchForImages(imageQuery: String) {
        if (imageQuery.isEmpty()) return
        _images.value = Resource.Loading

        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = response

        }
    }

    fun insertShoppingItem(name: String, amountString: String, priceString: String) {

        if (name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()){
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has an empty field.")
            return
        }
        if (name.length > Constants.MAX_NAME_LENGTH){
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has too long name.")
            return
        }
        if (priceString.length > Constants.MAX_PRICE_LENGTH){
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has too long price field.")
            return
        }
        val amount = try {
            amountString.toInt()
        }catch (e:Exception){
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has too high amount field.")
            return
        }

        val shoppingItem = ShoppingItem(
            name = name,
            amount = amount,
            price = priceString.toFloat(),
            imageUrl = curImageUrl.value
        )

        _insertShoppingItemStatus.value = Resource.Success(shoppingItem)
        setCurImageUrl("")

    }


}