package com.friendschat.textdetection.ui.extension

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun Fragment.showSnackbar(text: String) {
    Snackbar
        .make(
            this.requireActivity().findViewById(android.R.id.content),
            text, Snackbar.LENGTH_SHORT
        ).show()
}

fun Fragment.showSnackbar(text: String, actionTitle: String, actionListener: View.OnClickListener) {
    Snackbar
        .make(
            this.requireActivity().findViewById(android.R.id.content),
            text, Snackbar.LENGTH_SHORT
        )
        .setAction(actionTitle, actionListener)
        .show()
}

fun <T> Fragment.collectLifeCycleFlow(
    flow: Flow<T>,
    collectLatest: Boolean = true,
    collect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (collectLatest) {
                flow.collectLatest(collect)
            } else {
                flow.collect(collect)
            }
        }
    }
}