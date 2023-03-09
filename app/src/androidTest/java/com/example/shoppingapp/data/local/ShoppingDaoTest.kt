package com.example.shoppingapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@SmallTest
class ShoppingDaoTest {


    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: ShoppingDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        hiltAndroidRule.inject()
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