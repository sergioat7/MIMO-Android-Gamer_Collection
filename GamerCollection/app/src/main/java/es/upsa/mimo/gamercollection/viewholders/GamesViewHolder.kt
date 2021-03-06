package es.upsa.mimo.gamercollection.viewholders

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.game_item.view.*
import java.lang.Exception
import java.util.*

class GamesViewHolder(
    itemView: View,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>
) : RecyclerView.ViewHolder(itemView) {

    fun fillData(game: GameResponse, context: Context, sagaId: Int?) {

        val state = states.firstOrNull { it.id == game.state }
        state?.let {
            when (it.id) {
                Constants.pending -> itemView.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color4))
                Constants.inProgress -> itemView.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color5))
                Constants.finished -> itemView.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color3))
                else -> itemView.view_state.setBackgroundColor(Color.TRANSPARENT)
            }
        } ?: run {
            itemView.view_state.setBackgroundColor(Color.TRANSPARENT)
        }

        game.imageUrl?.let { url ->
            itemView.progress_bar_loading.visibility = View.VISIBLE
            Picasso.get()
                .load(url)
                .into(itemView.image_view_game, object : Callback {
                override fun onSuccess() {
                    itemView.progress_bar_loading.visibility = View.GONE
                }
                override fun onError(e: Exception?) {
                    itemView.progress_bar_loading.visibility = View.GONE
                }
            })
        } ?: run {
            itemView.image_view_game.setImageDrawable(null)
        }

        itemView.image_view_goty.visibility = if (game.goty) View.VISIBLE else View.GONE

        game.name?.let {
            itemView.text_view_name.text = it
            itemView.text_view_name.visibility = View.VISIBLE
        } ?: run {
            itemView.text_view_name.visibility = View.GONE
        }

        val platform = platforms.firstOrNull { it.id == game.platform }
        platform?.let {
            itemView.text_view_platform.text = it.name
            itemView.text_view_platform.visibility = View.VISIBLE
        } ?: run {
            itemView.text_view_platform.visibility = View.GONE
        }

        if (game.releaseDate != null && game.state == Constants.pending) {

            if (Date().before(game.releaseDate)) {
                itemView.image_view_calendar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unreleased))
            } else {
                itemView.image_view_calendar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.released))
            }
            itemView.image_view_calendar.visibility = View.VISIBLE
        } else {
            itemView.image_view_calendar.visibility = View.GONE
        }

        if (game.score > 0) {
            itemView.rating_bar.rating = (game.score / 2).toFloat()
            itemView.rating_bar.visibility = View.VISIBLE
        } else {
            itemView.rating_bar.visibility = View.GONE
        }

        itemView.image_view_arrow.visibility = if(sagaId != null) View.GONE else View.VISIBLE
        itemView.check_box.visibility = if(sagaId != null) View.VISIBLE else View.GONE
        itemView.check_box.isChecked = game.saga?.id  == sagaId
    }
}
