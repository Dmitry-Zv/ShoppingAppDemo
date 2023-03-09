package com.example.shoppingapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ShoppingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Query(value = "SELECT * FROM shopping_items")
    fun observeAllShoppingItems(): Flow<List<ShoppingItem>>

    @Query(value = "SELECT SUM(amount * price) FROM shopping_items")
    fun observeTotalPrice(): Flow<Float?>

}