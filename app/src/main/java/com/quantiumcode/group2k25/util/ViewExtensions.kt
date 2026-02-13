package com.quantiumcode.group2k25.util

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

/**
 * Applies status bar insets as top padding, preserving the view's original top padding.
 * Use on toolbars (with wrap_content height) or top-level content containers.
 */
fun View.applyTopInsets() {
    val initialPaddingTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
        v.updatePadding(top = initialPaddingTop + insets.top)
        windowInsets
    }
}

/**
 * Applies navigation bar insets as bottom padding, preserving the view's original bottom padding.
 * Use on scrollable containers that need to clear the navigation bar.
 */
fun View.applyBottomInsets() {
    val initialPaddingBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
        v.updatePadding(bottom = initialPaddingBottom + insets.bottom)
        windowInsets
    }
}

/**
 * Applies status bar insets as top margin instead of padding.
 * Useful for views where padding would affect the background.
 */
/**
 * Centers content vertically inside a ScrollView by adding top padding.
 * Call on the ScrollView's direct child (content container).
 * The scrollView parameter is the parent ScrollView.
 */
fun View.centerInScrollView(scrollView: ScrollView) {
    val contentView = this
    val originalBottomPadding = contentView.paddingBottom
    scrollView.viewTreeObserver.addOnGlobalLayoutListener {
        val available = scrollView.height - scrollView.paddingTop - scrollView.paddingBottom
        // Measure content without the centering padding
        val contentHeight = contentView.measuredHeight - contentView.paddingTop
        if (contentHeight < available) {
            val topPad = (available - contentHeight) / 2
            contentView.setPadding(
                contentView.paddingLeft,
                topPad,
                contentView.paddingRight,
                originalBottomPadding
            )
        } else {
            // Keyboard open or content taller — remove centering padding
            contentView.setPadding(
                contentView.paddingLeft,
                0,
                contentView.paddingRight,
                originalBottomPadding
            )
        }
    }
}

fun View.applyTopInsetsAsMargin() {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val initialTopMargin = params.topMargin
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
        params.topMargin = initialTopMargin + insets.top
        v.layoutParams = params
        windowInsets
    }
}
