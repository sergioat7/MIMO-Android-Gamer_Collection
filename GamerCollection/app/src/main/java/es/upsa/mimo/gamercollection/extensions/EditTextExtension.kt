package es.upsa.mimo.gamercollection.extensions

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.EditText
import androidx.preference.PreferenceManager
import java.util.*

fun EditText.setReadOnly(value: Boolean, inputType: Int, lineColor: Int) {

    isFocusable = !value
    isFocusableInTouchMode = !value
    isEnabled = !value
    this.setRawInputType(inputType)
    this.backgroundTintList = if (value) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(lineColor)
}

fun EditText.showDatePicker(context: Context) {

    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            val picker = getPicker(this, context)
            picker.show()
        }
    }
    this.setOnClickListener {
        val picker = getPicker(this, context)
        picker.show()
    }
}

// MARK - Private functions

private fun getPicker(editText: EditText, context: Context): DatePickerDialog {

    val calendar = Calendar.getInstance()
    val currentDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    val currentMonth: Int = calendar.get(Calendar.MONTH)
    val currentYear: Int = calendar.get(Calendar.YEAR)
    return DatePickerDialog(context, OnDateSetListener { _, year, month, day ->

        val newDay = if (day < 10) "0${day}" else day.toString()
        val newMonth = if (month < 9) "0${month+1}" else (month+1).toString()
        val language = PreferenceManager.getDefaultSharedPreferences(context).getString("language", Locale.getDefault().language) ?: Locale.getDefault().language
        val date = if (language == "es") "${newDay}-${newMonth}-${year}" else "${newMonth}-${newDay}-${year}"
        editText.setText(date)
    }, currentYear, currentMonth, currentDay)
}