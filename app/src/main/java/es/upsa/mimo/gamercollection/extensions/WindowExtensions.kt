/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 28/2/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat

fun Window.setStatusBarStyle(color: Int, lightStatusBar: Boolean) {
    statusBarColor = color
    WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = lightStatusBar
}