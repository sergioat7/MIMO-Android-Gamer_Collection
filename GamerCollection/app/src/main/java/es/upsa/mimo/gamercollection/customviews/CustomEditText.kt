package es.upsa.mimo.gamercollection.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.CustomEditTextBinding
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.custom_edit_text.view.*

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // MARK: - Private properties

    private val binding: CustomEditTextBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_edit_text,
        this,
        true
    )
    private lateinit var inputType: EditTextType

    // MARK: - Lifecycle methods

    init {
        setAttributes(attrs)
    }

    // MARK: - Public methods

    fun getText(): String {
        return binding.editText.getValue()
    }

    fun setText(text: String?) {
        binding.text = text
    }

    fun setReadOnly(notEditable: Boolean, lineColor: Int) {

        val type =
            if (notEditable) {
                InputType.TYPE_NULL
            } else {
                when (inputType) {
                    EditTextType.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    EditTextType.PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD
                    EditTextType.TEXT -> InputType.TYPE_CLASS_TEXT
                    EditTextType.NUMBER -> InputType.TYPE_CLASS_NUMBER
                    EditTextType.URL -> InputType.TYPE_TEXT_VARIATION_URI
                    EditTextType.DATE -> InputType.TYPE_NULL
                    EditTextType.NONE -> InputType.TYPE_NULL
                }
            }

        with(binding) {

            if (text == Constants.NO_VALUE && !notEditable) {
                text = null
            }
            editText.setReadOnly(notEditable, type, lineColor)
            editable = !notEditable
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.editText.setOnClickListener(l)
    }

    fun setDatePickerFormat(dateFormat: String) {
        binding.editText.showDatePicker(context, dateFormat)
    }

    // MARK: - Private methods

    @SuppressLint("Recycle")
    private fun setAttributes(attrs: AttributeSet?) {

        attrs?.let {

            val typed = context.obtainStyledAttributes(it, R.styleable.CustomEditText, 0, 0)
            inputType = EditTextType.valueOf(
                typed.getString(R.styleable.CustomEditText_customEditText_type) ?: "TEXT"
            )
            val hint = typed.getString(R.styleable.CustomEditText_customEditText_placeholder)
                ?: Constants.EMPTY_VALUE
            val font = typed.getResourceId(R.styleable.CustomEditText_customEditText_font, 0)
            val textSize = resources.getDimension(
                typed.getResourceId(
                    R.styleable.CustomEditText_customEditText_text_size,
                    0
                )
            )
            val icon = typed.getDrawable(R.styleable.CustomEditText_customEditText_icon)
            val imeOption = EditTextImeOption.valueOf(
                typed.getString(R.styleable.CustomEditText_customEditText_ime_option) ?: "NONE"
            )
            val paddingEnd =
                if (icon != null) (resources.getDimension(R.dimen.margin_larger) / resources.displayMetrics.density).toInt()
                else edit_text.paddingRight

            val type = when (inputType) {
                EditTextType.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                EditTextType.PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD
                EditTextType.TEXT -> InputType.TYPE_CLASS_TEXT
                EditTextType.NUMBER -> InputType.TYPE_CLASS_NUMBER
                EditTextType.URL -> InputType.TYPE_TEXT_VARIATION_URI
                EditTextType.DATE -> InputType.TYPE_NULL
                EditTextType.NONE -> InputType.TYPE_NULL
            }

            val option = when (imeOption) {
                EditTextImeOption.NEXT -> EditorInfo.IME_ACTION_NEXT
                EditTextImeOption.DONE -> EditorInfo.IME_ACTION_DONE
                EditTextImeOption.NONE -> EditorInfo.IME_ACTION_NONE
            }

            with(binding) {

                if (inputType == EditTextType.DATE) {
                    editText.showDatePicker(context)
                }
                editText.hint = hint
                editText.typeface = ResourcesCompat.getFont(context, font)
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                editText.setPadding(
                    editText.paddingLeft,
                    editText.paddingTop,
                    paddingEnd,
                    editText.paddingBottom
                )
                editText.setRawInputType(type)
                editText.imeOptions = option

                imageButton.setImageDrawable(icon)
                imageButton.setOnClickListener {
                    text = Constants.EMPTY_VALUE
                }

                editable = true
            }
        }
    }
}

enum class EditTextType {
    EMAIL, PASSWORD, TEXT, NUMBER, URL, DATE, NONE
}

enum class EditTextImeOption {
    NEXT, DONE, NONE
}