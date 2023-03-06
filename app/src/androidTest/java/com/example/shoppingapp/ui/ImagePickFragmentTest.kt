package com.example.shoppingapp.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.pressBack
import androidx.test.filters.MediumTest
import com.example.shoppingapp.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ImagePickFragmentTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private lateinit var navController: NavController

    @Before
    fun setup(){
        hiltRule.inject()
        navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ImagePickFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }
    }

//    @Test
//    fun pressBackButton_popBackStackWithEmptyImageUrl(){
//        pressBack()
//        verify(navController).
//
//    }

}