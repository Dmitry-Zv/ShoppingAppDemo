package com.example.shoppingapp.ui.events

import com.example.shoppingapp.data.local.ShoppingItem

interface ActionEvent {
    fun obtainedEvent(event: ViewModelEvent)
}

sealed class ViewModelEvent {
    object ObtainInitial : ViewModelEvent()
    data class InsertShoppingItem(
        val name: String,
        val amountString: String,
        val priceString: String
    ) : ViewModelEvent()

    data class DeleteShoppingItem(val shoppingItem: ShoppingItem) : ViewModelEvent()
    data class SearchImages(val imageQuery: String) : ViewModelEvent()
    data class SetCurImageUrl(val imageUrl: String) : ViewModelEvent()
    data class InsertShoppingItemIntoDb(val shoppingItem: ShoppingItem) : ViewModelEvent()

}