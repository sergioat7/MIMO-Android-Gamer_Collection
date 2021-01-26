package es.upsa.mimo.gamercollection.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import es.upsa.mimo.gamercollection.R
import kotlinx.android.synthetic.main.state_button.view.*

class StateButton: ConstraintLayout {

    // MARK: - Lifecycle methods

    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.state_button, this, true)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

        LayoutInflater.from(context).inflate(R.layout.state_button, this, true)
        setAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int) : super(context, attrs, defStyle) {

        LayoutInflater.from(context).inflate(R.layout.state_button, this, true)
        setAttributes(attrs)
    }

    // MARK: - Private methods

    @SuppressLint("Recycle")
    private fun setAttributes(attrs: AttributeSet?) {

        attrs?.let {

            val typed = context.obtainStyledAttributes(it, R.styleable.StateButton, 0, 0)
            val title = typed.getString(R.styleable.StateButton_stateButton_title) ?: ""
            val subtitle = typed.getString(R.styleable.StateButton_stateButton_subtitle) ?: ""
            val color = typed.getDrawable(R.styleable.StateButton_stateButton_color)
            val background = typed.getDrawable(R.styleable.StateButton_stateButton_background)

            text_view_title.text = title
            text_view_title.visibility = if(title.isEmpty()) View.GONE else View.VISIBLE
            text_view_subtitle.text = subtitle
            text_view_subtitle.visibility = if(subtitle.isEmpty()) View.GONE else View.VISIBLE
            line_view_color.background = color
            layout_state_button.background = background
        }
    }
}