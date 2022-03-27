/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 27/3/2022
 */

package es.upsa.mimo.gamercollection.extensions

import androidx.core.view.doOnLayout
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.MenuAdapter
import es.upsa.mimo.gamercollection.databinding.CustomDropdownTextInputLayoutBinding
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.CustomDropdownType

fun CustomDropdownTextInputLayoutBinding.setHintStyle(id: Int) {
    textInputLayout.doOnLayout {
        textInputLayout.setHintTextAppearance(id)
    }
}

fun CustomDropdownTextInputLayoutBinding.getPosition(): Int {
    return (materialAutoCompleteTextView.adapter as MenuAdapter).values.indexOf(
        materialAutoCompleteTextView.text.toString()
    )
}

fun CustomDropdownTextInputLayoutBinding.getValue(): String {
    return materialAutoCompleteTextView.text.toString().trimStart().trimEnd()
}

fun CustomDropdownTextInputLayoutBinding.setValue(currentKey: String?, type: CustomDropdownType) {

    val values = when (type) {
        CustomDropdownType.FORMAT -> Constants.FORMATS.map { it.name }
        CustomDropdownType.GENRE -> Constants.GENRES.map { it.name }
        CustomDropdownType.PLATFORM -> Constants.PLATFORMS.map { it.name }
        CustomDropdownType.STATE -> Constants.STATES.map { it.name }
        CustomDropdownType.SORT_PARAM -> root.context.resources.getStringArray(R.array.sort_param_values)
            .toList()
        CustomDropdownType.SORT_ORDER -> root.context.resources.getStringArray(R.array.sort_order_values)
            .toList()
        CustomDropdownType.APP_THEME -> root.context.resources.getStringArray(R.array.app_theme_values)
            .toList()
    }

    if (materialAutoCompleteTextView.adapter == null) {
        materialAutoCompleteTextView.setAdapter(MenuAdapter(root.context, values))
    }

    currentKey?.let { key ->
        val keys = when (type) {
            CustomDropdownType.FORMAT -> Constants.FORMATS.map { it.id }
            CustomDropdownType.GENRE -> Constants.GENRES.map { it.id }
            CustomDropdownType.PLATFORM -> Constants.PLATFORMS.map { it.id }
            CustomDropdownType.STATE -> Constants.STATES.map { it.id }
            CustomDropdownType.SORT_PARAM -> root.context.resources.getStringArray(R.array.sort_param_keys)
                .toList()
            CustomDropdownType.SORT_ORDER -> root.context.resources.getStringArray(R.array.sort_order_keys)
                .toList()
            CustomDropdownType.APP_THEME -> root.context.resources.getStringArray(R.array.app_theme_values)
                .toList()
        }
        values.getOrNull(keys.indexOf(key))?.let { value ->
            materialAutoCompleteTextView.setText(value, false)
        }
    }
}