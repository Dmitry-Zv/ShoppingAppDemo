package com.example.shoppingapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shoppingapp.MainCoroutineRule
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.others.Constants
import com.example.shoppingapp.others.Resource
import com.example.shoppingapp.repositories.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var fakRepository: FakeShoppingRepository

    @Before
    fun setup() {
        fakRepository = FakeShoppingRepository()
        viewModel = ShoppingViewModel(fakRepository)
    }


    @Test
    fun `insert shopping item with empty field, returns error`() = runTest {
        viewModel.insertShoppingItem(name = "name", amountString = "", priceString = "4.3")
        assertThat(viewModel.insertShoppingItemStatus.value).isEqualTo(Resource.Error("Shopping item has an empty field."))
    }


    @Test
    fun `insert shopping item with too long name, returns error`() = runTest {
        val stringName = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append("X")
            }
        }

        viewModel.insertShoppingItem(name = stringName, amountString = "12", priceString = "4.3")
        assertThat(viewModel.insertShoppingItemStatus.value).isEqualTo(Resource.Error("Shopping item has too long name."))
    }

    @Test
    fun `insert shopping item with too long price, returns error`() = runTest {
        val stringPrice = buildString {
            for (i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append("2")
            }
        }

        viewModel.insertShoppingItem(name = "name", amountString = "12", priceString = stringPrice)
        assertThat(viewModel.insertShoppingItemStatus.value).isEqualTo(Resource.Error("Shopping item has too long price field."))
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() = runTest {
        viewModel.insertShoppingItem(
            name = "name",
            amountString = "99999999999999999999",
            priceString = "4.5"
        )
        assertThat(viewModel.insertShoppingItemStatus.value).isEqualTo(Resource.Error("Shopping item has too high amount field."))
    }

    @Test
    fun `insert shopping item with valid input, returns success`() = runTest {
        viewModel.insertShoppingItem(name = "name", amountString = "4", priceString = "4.5")
        assertThat(viewModel.insertShoppingItemStatus.value).isEqualTo(
            Resource.Success(
                data = ShoppingItem(
                    name = "name",
                    amount = 4,
                    price = 4.5F,
                    imageUrl = viewModel.curImageUrl.value
                )
            )
        )
    }

    @Test
    fun `after success insert shoppingItem, returns empty imageUrl`() {
        viewModel.setCurImageUrl("")
        assertThat(viewModel.curImageUrl.value).isEqualTo("")
    }

    @Test
    fun `if curImageUrl can be observe, return imageUrl`() = runTest {
        viewModel.setCurImageUrl("imageUrl")
        assertThat(viewModel.curImageUrl.value).isEqualTo("imageUrl")
    }


}