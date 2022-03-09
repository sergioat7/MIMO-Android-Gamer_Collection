package es.upsa.mimo.gamercollection.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.StateButtonBinding
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.utils.Constants

class StateButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    //region Private properties
    private val binding: StateButtonBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.state_button,
        this,
        true
    )
    //endregion

    //region Lifecycle methods
    init {
        setAttributes(attrs)
    }
    //endregion

    //region Public methods
    fun setSubtitle(text: String?) {
        binding.subtitle = text
    }
    //endregion

    //region Private methods
    @SuppressLint("Recycle")
    private fun setAttributes(attrs: AttributeSet?) {

        attrs?.let {

            val typed = context.obtainStyledAttributes(it, R.styleable.StateButton, 0, 0)

            with(binding) {
                title = typed.getString(R.styleable.StateButton_stateButton_title)
                    ?: Constants.EMPTY_VALUE
                subtitle = typed.getString(R.styleable.StateButton_stateButton_subtitle)
                    ?: Constants.EMPTY_VALUE
                lineColor =
                    typed.getDrawable(R.styleable.StateButton_stateButton_color)
                background =
                    typed.getDrawable(R.styleable.StateButton_stateButton_background)
            }
        }
    }
    //endregion
}