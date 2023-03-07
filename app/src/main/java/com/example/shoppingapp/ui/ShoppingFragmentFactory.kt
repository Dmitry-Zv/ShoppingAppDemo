package com.example.shoppingapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.shoppingapp.adapters.ImageAdapter
import javax.inject.Inject

class ShoppingFragmentFactory @Inject constructor(
    private val adapter: ImageAdapter
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
       return when(className){
           ImagePickFragment::class.java.name -> ImagePickFragment(adapter)
           else -> super.instantiate(classLoader, className)
       }

    }
}