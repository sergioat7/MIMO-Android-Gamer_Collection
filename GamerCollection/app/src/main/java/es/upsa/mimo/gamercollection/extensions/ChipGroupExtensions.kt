/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 2/3/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import es.upsa.mimo.gamercollection.R

fun ChipGroup.addChip(inflater: LayoutInflater, id: String, text: String?) {
    (inflater.inflate(
        R.layout.content_chip,
        this,
        false
    ) as Chip).also { chip ->
        chip.tag = id
        chip.text = text
        this.addView(chip)
    }
}