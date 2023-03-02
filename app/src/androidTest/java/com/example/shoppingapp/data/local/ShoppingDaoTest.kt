package com.example.shoppingapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.internal.builders.JUnit4Builder
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertShoppingItem() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 5,
            price = 3.4F,
            imageUrl = "imageUrl",
            id = 1
        )

        dao.insertShoppingItem(shoppingItem)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            dao.observeAllShoppingItems().collect {
                assertThat(it).contains(shoppingItem)

            }
        }
    }

    @Test
    fun deleteShoppingItem() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 5,
            price = 3.4F,
            imageUrl = "imageUrl",
            id = 1
        )

        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            dao.observeAllShoppingItems().collect {
                assertThat(it).doesNotContain(shoppingItem)

            }
        }
    }

    @Test
    fun observeTotalPrice() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 5,
            price = 5F,
            imageUrl = "imageUrl",
            id = 1
        )
        val shoppingItem2 = ShoppingItem(
            name = "name",
            amount = 3,
            price = 3.4F,
            imageUrl = "imageUrl",
            id = 2
        )
        val shoppingItem3 = ShoppingItem(
            name = "name",
            amount = 2,
            price = 4F,
            imageUrl = "imageUrl",
            id = 3
        )

        dao.insertShoppingItem(shoppingItem)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            dao.observeTotalPrice().collect {
                assertThat(it).isEqualTo(
                    5 * 5F + 3 * 3.4F + 2 * 4F
                )
            }
        }

    }

}