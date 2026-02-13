package com.quantiumcode.group2k25.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG, action: String? = null, onAction: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, duration)
    if (action != null && onAction != null) {
        snackbar.setAction(action) { onAction() }
    }
    snackbar.show()
}
