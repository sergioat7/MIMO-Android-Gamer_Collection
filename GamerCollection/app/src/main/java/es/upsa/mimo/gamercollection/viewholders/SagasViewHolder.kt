package es.upsa.mimo.gamercollection.viewholders

import android.content.Context
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.models.SagaResponse
import kotlinx.android.synthetic.main.saga_item.view.*

class SagasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun fillData(saga: SagaResponse, context: Context, expanded: Boolean) {

        itemView.edit_text_name.setText(saga.name)
        itemView.edit_text_name.setReadOnly(true, InputType.TYPE_NULL, 0)
        val image = if (expanded) R.drawable.ic_keyboard_arrow_down_white_24dp else R.drawable.ic_keyboard_arrow_up_white_24dp
        itemView.image_view_arrow.setImageDrawable(ContextCompat.getDrawable(context, image))
        itemView.image_view_arrow.visibility = if (saga.games.isNotEmpty()) View.VISIBLE else View.GONE
    }
}