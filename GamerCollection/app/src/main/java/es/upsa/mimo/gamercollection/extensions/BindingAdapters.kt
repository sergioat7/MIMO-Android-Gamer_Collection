/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/3/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.customviews.ImageViewWithLoading
import me.zhanghai.android.materialratingbar.MaterialRatingBar

@BindingAdapter(
    value = ["src", "center", "placeholder", "radius", "loadingColor"],
    requireAll = false
)
fun setImageUri(
    imageViewWithLoading: ImageViewWithLoading,
    image: String?,
    center: Boolean?,
    placeholder: Drawable?,
    radius: Float?,
    loadingColor: Int?
) {

    loadingColor?.let {
        imageViewWithLoading.binding.progressBarImageLoading.indeterminateTintList =
            ColorStateList.valueOf(it)
    }
    var img = image?.replace("http:", "https:") ?: "-"
    if (img.isBlank()) img = "-"
    Picasso
        .get()
        .load(img)
        .apply {
            if (center == true) {
                fit().centerCrop()
            }
            placeholder?.let {
                error(it)
            }
        }
        .into(imageViewWithLoading.binding.imageView, object : Callback {

            override fun onSuccess() {
                if (radius != null && radius >= 0) {
                    imageViewWithLoading.binding.imageView.apply {
                        this.setImageDrawable(this.getRoundImageView(radius))
                    }
                }
                imageViewWithLoading.binding.progressBarImageLoading.visibility = View.GONE
            }

            override fun onError(e: Exception) {
                imageViewWithLoading.binding.progressBarImageLoading.visibility = View.GONE
            }
        })
}

@BindingAdapter("rating")
fun setRating(ratingBar: MaterialRatingBar, rating: Double?) {
    ratingBar.rating = rating?.toFloat() ?: 0F
}