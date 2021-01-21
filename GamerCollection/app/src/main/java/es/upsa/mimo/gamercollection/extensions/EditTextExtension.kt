package es.upsa.mimo.gamercollection.extensions

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.EditText
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
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
    return DatePickerDialog(context, { _, year, month, day ->

        val newDay = if (day < 10) "0${day}" else day.toString()
        val newMonth = if (month < 9) "0${month+1}" else (month+1).toString()
        val newDate = "${year}-${newMonth}-${newDay}"

        val sharedPreferencesHandler = SharedPreferencesHandler(
            context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
        val language = sharedPreferencesHandler.getLanguage()

        val date = Constants.stringToDate(
            newDate,
            Constants.DATE_FORMAT,
            language
        )
        val dateString = Constants.dateToString(
            date,
            Constants.getDateFormatToShow(sharedPreferencesHandler),
            language
        )

        editText.setText(dateString)
    }, currentYear, currentMonth, currentDay)
}