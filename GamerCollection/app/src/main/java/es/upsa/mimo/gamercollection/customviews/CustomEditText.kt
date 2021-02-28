package es.upsa.mimo.gamercollection.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.custom_edit_text.view.*

class CustomEditText: ConstraintLayout {

    // MARK: - Private properties

    private lateinit var inputType: EditTextType

    // MARK: - Lifecycle methods

    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.custom_edit_text, this, true)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

        LayoutInflater.from(context).inflate(R.layout.custom_edit_text, this, true)
        setAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int) : super(context, attrs, defStyle) {

        LayoutInflater.from(context).inflate(R.layout.custom_edit_text, this, true)
        setAttributes(attrs)
    }

    // MARK: - Public methods

    fun getText(): String {
        return edit_text.text.toString()
    }

    fun setText(text: String?) {
        edit_text.setText(text)
    }

    fun setReadOnly(value: Boolean, lineColor: Int) {

        val type =
            if (value) {
                InputType.TYPE_NULL
            } else {
                when(inputType) {
                    EditTextType.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    EditTextType.PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD
                    EditTextType.TEXT -> InputType.TYPE_CLASS_TEXT
                    EditTextType.NUMBER -> InputType.TYPE_CLASS_NUMBER
                    EditTextType.URL -> InputType.TYPE_TEXT_VARIATION_URI
                    EditTextType.DATE -> InputType.TYPE_NULL
                    EditTextType.NONE -> InputType.TYPE_NULL
                }
            }
        edit_text.setReadOnly(value, type, lineColor)
        image_button.visibility = if(!value) View.VISIBLE else View.GONE
    }

    override fun setOnClickListener(l: OnClickListener?) {
        edit_text.setOnClickListener(l)
    }

    // MARK: - Private methods

    @SuppressLint("Recycle")
    private fun setAttributes(attrs: AttributeSet?) {

        attrs?.let {

            val typed = context.obtainStyledAttributes(it, R.styleable.CustomEditText, 0, 0)
            inputType = EditTextType.valueOf(typed.getString(R.styleable.CustomEditText_customEditText_type) ?: "TEXT")
            val hint = typed.getString(R.styleable.CustomEditText_customEditText_placeholder) ?: Constants.EMPTY_VALUE
            val font = typed.getResourceId(R.styleable.CustomEditText_customEditText_font, 0)
            val textSize = resources.getDimension(typed.getResourceId(R.styleable.CustomEditText_customEditText_text_size, 0))
            val icon = typed.getDrawable(R.styleable.CustomEditText_customEditText_icon)

            edit_text.hint = hint
            edit_text.typeface = ResourcesCompat.getFont(context, font)
            edit_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            val paddingEnd =
                if (icon != null) (resources.getDimension(R.dimen.margin_larger) / resources.displayMetrics.density).toInt()
                else edit_text.paddingRight
            edit_text.setPadding(
                edit_text.paddingLeft,
                edit_text.paddingTop,
                paddingEnd,
                edit_text.paddingBottom
            )
            image_button.setImageDrawable(icon)
            image_button.setOnClickListener {
                edit_text.setText("")
            }

            if (inputType == EditTextType.DATE) {
                edit_text.showDatePicker(context)
            }
        }
    }
}
enum class EditTextType {
    EMAIL, PASSWORD, TEXT, NUMBER, URL, DATE, NONE
}