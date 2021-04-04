package es.upsa.mimo.gamercollection.extensions

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.onFocusChange(onFocusChange: () -> Unit) {

    this.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            onFocusChange()
        }
    }
}

fun EditText.clearErrors() {
    this.error = null
}

fun EditText.setReadOnly(value: Boolean, inputType: Int, lineColor: Int) {

    isFocusable = !value
    isFocusableInTouchMode = !value
    isEnabled = !value
    this.setRawInputType(inputType)
    this.backgroundTintList =
        if (value) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(lineColor)
}

fun EditText.showDatePicker(context: Context, dateFormat: String? = null) {

    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            val picker = getPicker(this, context, dateFormat)
            picker.show()
        }
    }
    this.setOnClickListener {
        val picker = getPicker(this, context, dateFormat)
        picker.show()
    }
}

fun EditText.getValue(): String {
    return this.text.toString().trimStart().trimEnd()
}

//region Private functions
private fun getPicker(editText: EditText, context: Context, dateFormat: String?): DatePickerDialog {

    val calendar = Calendar.getInstance()
    val currentDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    val currentMonth: Int = calendar.get(Calendar.MONTH)
    val currentYear: Int = calendar.get(Calendar.YEAR)
    return DatePickerDialog(context, { _, year, month, day ->

        val newDay = if (day < 10) "0${day}" else day.toString()
        val newMonth = if (month < 9) "0${month + 1}" else (month + 1).toString()
        val newDate = "${year}-${newMonth}-${newDay}"

        val sharedPreferencesHandler = SharedPreferencesHandler(
            context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
        val language = sharedPreferencesHandler.getLanguage()
        val dateFormatToShow = dateFormat ?: Constants.getDateFormatToShow(language)

        val date = Constants.stringToDate(
            newDate,
            Constants.DATE_FORMAT,
            language
        )
        val dateString = Constants.dateToString(
            date,
            dateFormatToShow,
            language
        )

        editText.setText(dateString)
    }, currentYear, currentMonth, currentDay)
}
//endregion