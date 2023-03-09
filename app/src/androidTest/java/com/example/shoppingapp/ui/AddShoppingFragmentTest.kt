package com.example.shoppingapp.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.shoppingapp.MainCoroutineAndroidRule
import com.example.shoppingapp.R
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.launchFragmentInHiltContainer
import com.example.shoppingapp.repositories.FakeShoppingAndroidRepository
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

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class AddShoppingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @get:Rule
    var mainCoroutineAndroidRule = MainCoroutineAndroidRule()

    @Inject
    lateinit var fragmentFactory: ShoppingFragmentFactory


    @Before
    fun setup() {
        hiltRule.inject()

    }


    @Test
    fun pressBackButton_popBackStack() {
        val imageUrl = ""
        val testViewModel = ShoppingViewModel(FakeShoppingAndroidRepository())
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShoppingFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = testViewModel
        }
        pressBack()
        verify(navController).popBackStack()
        assertThat(testViewModel.curImageUrl.value).isEqualTo(imageUrl)
    }

    @Test
    fun ivShoppingImage_navigateToImagePickFragment() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShoppingFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.ivShoppingImage)).perform(click())
        verify(navController).navigate(R.id.action_addShoppingFragment_to_imagePickFragment)
    }


    @Test
    fun clickInsertIntoDb_shoppingItemInsertIntoDb() = runTest {
        val testViewModel = ShoppingViewModel(FakeShoppingAndroidRepository())

        launchFragmentInHiltContainer<AddShoppingFragment>(fragmentFactory = fragmentFactory) {
            viewModel = testViewModel
        }

        onView(withId(R.id.etShoppingItemName)).perform(replaceText("shopping item"))
        onView(withId(R.id.etShoppingItemAmount)).perform(replaceText("10"))
        onView(withId(R.id.etShoppingItemPrice)).perform(replaceText("4.5"))
        onView(withId(R.id.btnAddShoppingItem)).perform(click())

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.shoppingItems.collect {
                assertThat(it).contains(
                    ShoppingItem(
                        name = "shopping item",
                        amount = 10,
                        price = 4.5F,
                        imageUrl = testViewModel.curImageUrl.value
                    )
                )
            }
        }

    }


}