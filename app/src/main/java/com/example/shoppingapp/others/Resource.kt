package com.example.shoppingapp.others

sealed class Resource<out T : Any> {
    object Initial : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val msg: String) : Resource<Nothing>()

}