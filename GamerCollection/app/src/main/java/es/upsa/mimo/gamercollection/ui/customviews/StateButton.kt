package es.upsa.mimo.gamercollection.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.StateButtonBinding

class StateButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    //region Public properties
    val binding: StateButtonBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.state_button,
        this,
        true
    )
    //endregion
}