/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/3/2022
 */

package es.upsa.mimo.gamercollection.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.ImageViewWithLoadingBinding

class ImageViewWithLoading @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: ImageViewWithLoadingBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.image_view_with_loading,
        this,
        true
    )
}