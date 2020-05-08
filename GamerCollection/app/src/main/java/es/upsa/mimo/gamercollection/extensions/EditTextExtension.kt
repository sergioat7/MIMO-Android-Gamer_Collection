package es.upsa.mimo.gamercollection.extensions

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.EditText
import es.upsa.mimo.gamercollection.R

fun EditText.setReadOnly(value: Boolean, inputType: Int, lineColor: Int) {

    isFocusable = !value
    isFocusableInTouchMode = !value
    this.setRawInputType(inputType)
    this.backgroundTintList = if (value) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(lineColor)
}