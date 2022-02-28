/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 28/2/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import es.upsa.mimo.gamercollection.R

fun Activity.hideSoftKeyboard() {
    currentFocus?.let { currentFocus ->

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    } ?: return
}

fun Context?.isDarkMode(): Boolean {
    return this?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun Context.getImageForPegi(pegi: String?): Drawable? {
    return when (pegi) {

        "+3" -> ContextCompat.getDrawable(this, R.drawable.pegi3)
        "+7" -> ContextCompat.getDrawable(this, R.drawable.pegi7)
        "+12" -> ContextCompat.getDrawable(this, R.drawable.pegi12)
        "+16" -> ContextCompat.getDrawable(this, R.drawable.pegi16)
        "+18" -> ContextCompat.getDrawable(this, R.drawable.pegi18)
        else -> null
    }
}