package com.example.shoppingapp.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.shoppingapp.R
import com.example.shoppingapp.launchFragmentInHiltContainer
import com.example.shoppingapp.repositories.FakeShoppingAndroidRepository
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class AddShoppingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Before
    fun setup() {
        hiltRule.inject()

    }


    @Test
    fun pressBackButton_popBackStack() {
        val imageUrl = ""
        val testViewModel = ShoppingViewModel(FakeShoppingAndroidRepository())
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShoppingFragment> {
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
        launchFragmentInHiltContainer<AddShoppingFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.ivShoppingImage)).perform(click())
        verify(navController).navigate(R.id.action_addShoppingFragment_to_imagePickFragment)
    }


}