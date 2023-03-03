package com.example.wasim

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.lifecycleAwareCollect(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>,
) {
    val stateFlow = this
    with(lifecycleOwner) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                stateFlow.collect(collector)
            }
        }
    }
}
