package es.upsa.mimo.gamercollection.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import es.upsa.mimo.gamercollection.R
import kotlinx.android.synthetic.main.layout_empty_list.view.*

class EmptyListLayout : ConstraintLayout {

    //region Lifecycle methods
    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.layout_empty_list, this, true)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_empty_list, this, true)
        setAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {

        LayoutInflater.from(context).inflate(R.layout.layout_empty_list, this, true)
        setAttributes(attrs)
    }
    //endregion

    //region Private methods
    @SuppressLint("Recycle")
    private fun setAttributes(attrs: AttributeSet?) {

        attrs?.let {

            val typed = context.obtainStyledAttributes(it, R.styleable.EmptyListLayout, 0, 0)
            val image = typed.getDrawable(R.styleable.EmptyListLayout_emptyListLayout_image)

            image_view_empty_list.setImageDrawable(image)
        }
    }
    //endregion
}