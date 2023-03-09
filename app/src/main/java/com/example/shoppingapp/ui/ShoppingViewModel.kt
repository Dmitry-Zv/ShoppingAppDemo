package com.example.shoppingapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.data.remote.ImageResponse
import com.example.shoppingapp.others.Constants
import com.example.shoppingapp.others.Resource
import com.example.shoppingapp.repositories.ShoppingRepository
import com.example.shoppingapp.ui.events.ActionEvent
import com.example.shoppingapp.ui.events.ViewModelEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ShoppingRepository
) : ViewModel(), ActionEvent {

    val shoppingItems = repository.observeAllShoppingItems()
    val totalPrice = repository.observeTotalPrice()


    private val _images = MutableStateFlow<Resource<ImageResponse>>(Resource.Initial)
    val images = _images.asStateFlow()

    private val _curImageUrl = MutableStateFlow("")
    val curImageUrl = _curImageUrl.asStateFlow()

    private val _insertShoppingItemStatus =
        MutableStateFlow<Resource<ShoppingItem>>(Resource.Initial)
    val insertShoppingItemStatus = _insertShoppingItemStatus.asStateFlow()


    override fun obtainedEvent(event: ViewModelEvent) {

        when (event) {
            is ViewModelEvent.ObtainInitial -> performInitial()
            is ViewModelEvent.SearchImages -> searchForImages(imageQuery = event.imageQuery)
            is ViewModelEvent.InsertShoppingItem -> insertShoppingItem(
                name = event.name,
                amountString = event.amountString,
                priceString = event.priceString
            )
            is ViewModelEvent.DeleteShoppingItem -> deleteShoppingItem(shoppingItem = event.shoppingItem)
            is ViewModelEvent.SetCurImageUrl -> setCurImageUrl(imageUrl = event.imageUrl)
            is ViewModelEvent.InsertShoppingItemIntoDb -> insertShoppingItemIntoDb(shoppingItem = event.shoppingItem)
        }
    }

    private fun performInitial() {
        viewModelScope.launch {
            _images.value = Resource.Initial
            _insertShoppingItemStatus.value = Resource.Initial
        }
    }

    private fun setCurImageUrl(imageUrl: String) {
        _curImageUrl.value = imageUrl
    }

    private fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    private fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    private fun searchForImages(imageQuery: String) {
        if (imageQuery.isEmpty()) return
        _images.value = Resource.Loading

        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = response

        }
    }

    private fun insertShoppingItem(name: String, amountString: String, priceString: String) {

        if (name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()) {
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has an empty field.")
            return
        }
        if (name.length > Constants.MAX_NAME_LENGTH) {
            _insertShoppingItemStatus.value = Resource.Error("Shopping item has too long name.")
            return
        }
        if (priceString.length > Constants.MAX_PRICE_LENGTH) {
            _insertShoppingItemStatus.value =
                Resource.Error("Shopping item has too long price field.")
            return
        }
        val amount = try {
            amountString.toInt()
        } catch (e: Exception) {
            _insertShoppingItemStatus.value =
                Resource.Error("Shopping item has too high amount field.")
            return
        }

        val shoppingItem = ShoppingItem(
            name = name,
            amount = amount,
            price = priceString.toFloat(),
            imageUrl = curImageUrl.value
        )
        insertShoppingItemIntoDb(shoppingItem = shoppingItem)
        _insertShoppingItemStatus.value = Resource.Success(shoppingItem)
        setCurImageUrl("")

    }


}