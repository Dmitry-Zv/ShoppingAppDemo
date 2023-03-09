package com.example.shoppingapp.others

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


fun <T> Fragment.collectFlow(stateFlow: StateFlow<T>, collect: (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            stateFlow.collect {
                collect(it)
            }
        }
    }
}