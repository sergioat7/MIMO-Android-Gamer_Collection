package es.upsa.mimo.gamercollection.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
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

fun EditText.showDatePicker(activity: FragmentActivity, dateFormat: String? = null) {

    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            val datePicker = getPicker(this, context, dateFormat)
            datePicker.show(activity.supportFragmentManager, "")
        }
    }
    this.setOnClickListener {
        val datePicker = getPicker(this, context, dateFormat)
        datePicker.show(activity.supportFragmentManager, "")
    }
}

fun EditText.getValue(): String {
    return this.text.toString().trimStart().trimEnd()
}

//region Private functions
private fun getPicker(editText: EditText, context: Context, dateFormat: String?): MaterialDatePicker<Long> {

    val language = SharedPreferencesHelper.language
    val dateFormatToShow = dateFormat ?: SharedPreferencesHelper.dateFormatToShow

    val currentDateInMillis = editText.text.toString().toDate(
        dateFormatToShow,
        language,
        TimeZone.getTimeZone("UTC")
    )?.time ?: MaterialDatePicker.todayInUtcMilliseconds()

    return MaterialDatePicker.Builder
        .datePicker()
        .setTheme(R.style.ThemeOverlay_GamerCollection_MaterialCalendar)
        .setTitleText(context.resources.getString(R.string.game_detail_select_date))
        .setSelection(currentDateInMillis)
        .build().apply {
            addOnPositiveButtonClickListener {

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it

                val dateString = calendar.time.toString(
                    dateFormatToShow,
                    language
                )

                editText.setText(dateString)
            }
        }
}
//endregion