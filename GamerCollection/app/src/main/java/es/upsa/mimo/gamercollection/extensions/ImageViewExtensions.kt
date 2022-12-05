/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/3/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

fun ImageView.getRoundImageView(radius: Float): RoundedBitmapDrawable {

    val imageBitmap = (drawable as BitmapDrawable).bitmap
    val imageDrawable = RoundedBitmapDrawableFactory.create(context.resources, imageBitmap)
    imageDrawable.isCircular = true
    imageDrawable.cornerRadius = radius
    return imageDrawable
}