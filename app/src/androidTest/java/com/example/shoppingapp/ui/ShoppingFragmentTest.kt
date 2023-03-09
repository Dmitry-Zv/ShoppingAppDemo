package com.example.shoppingapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.shoppingapp.MainCoroutineAndroidRule
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.ShoppingItemAdapter
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.launchFragmentInHiltContainer
import com.example.shoppingapp.ui.events.ViewModelEvent
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ShoppingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineAndroidRule = MainCoroutineAndroidRule()


    @Inject
    lateinit var testFragmentFactory: TestShoppingFragmentFactory


    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun swipeShoppingItem_deleteItemInDb() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 5,
            price = 4F,
            imageUrl = "imageUrl",
            id = 1
        )
        var testViewModel: ShoppingViewModel? = null
        launchFragmentInHiltContainer<ShoppingFragment>(fragmentFactory = testFragmentFactory) {
            testViewModel = viewModel
            viewModel?.obtainedEvent(event = ViewModelEvent.InsertShoppingItemIntoDb(shoppingItem = shoppingItem))

        }

        onView(withId(R.id.rvShoppingItems)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ShoppingItemAdapter.ShoppingItemViewHolder>(
                0,
                swipeLeft()
            )
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel?.shoppingItems?.collect {
                assertThat(it).doesNotContain(shoppingItem)
            }
        }


    }

    @Test
    fun clickAddShoppingItemButton_navigateToAddShoppingItemFragment() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ShoppingFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.fabAddShoppingItem)).perform(click())

        verify(navController).navigate(R.id.action_shoppingFragment_to_addShoppingFragment)
    }
}