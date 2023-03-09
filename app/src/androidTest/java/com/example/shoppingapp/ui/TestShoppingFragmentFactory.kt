package com.example.shoppingapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.example.shoppingapp.adapters.ImageAdapter
import com.example.shoppingapp.adapters.ShoppingItemAdapter
import com.example.shoppingapp.repositories.FakeShoppingAndroidRepository
import javax.inject.Inject

class TestShoppingFragmentFactory @Inject constructor(
    private val adapter: ImageAdapter,
    private val glide: RequestManager,
    private val shoppingAdapter: ShoppingItemAdapter
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ImagePickFragment::class.java.name -> ImagePickFragment(adapter)
            AddShoppingFragment::class.java.name -> AddShoppingFragment(glide)
            ShoppingFragment::class.java.name -> ShoppingFragment(
                shoppingAdapter,
                ShoppingViewModel((FakeShoppingAndroidRepository()))
            )
            else -> super.instantiate(classLoader, className)
        }

    }
}