package es.upsa.mimo.gamercollection.viewholders

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.game_item.view.*

class GamesViewHolder(
    itemView: View,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>
) : RecyclerView.ViewHolder(itemView) {

    // MARK: - Public methods

    fun fillData(game: GameResponse, context: Context, sagaId: Int?) {

        val state = states.firstOrNull { it.id == game.state }
        state?.let {
            when (it.id) {
                Constants.PENDING_STATE -> itemView.view_state.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPending
                    )
                )
                Constants.IN_PROGRESS_STATE -> itemView.view_state.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorInProgress
                    )
                )
                Constants.FINISHED_STATE -> itemView.view_state.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorFinished
                    )
                )
                else -> itemView.view_state.setBackgroundColor(Color.TRANSPARENT)
            }
        } ?: run {
            itemView.view_state.setBackgroundColor(Color.TRANSPARENT)
        }

        val image = game.imageUrl ?: "-"
        val errorImage =
            if (Constants.isDarkMode(context)) R.drawable.ic_default_game_cover_dark else R.drawable.ic_default_game_cover_light
        val loading = itemView.progress_bar_loading
        loading.visibility = View.VISIBLE
        Picasso
            .get()
            .load(image)
            .fit()
            .centerCrop()
            .error(errorImage)
            .into(itemView.image_view_game, object : Callback {

                override fun onSuccess() {
                    loading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    loading.visibility = View.GONE
                }
            })

        itemView.image_view_goty.visibility = if (game.goty) View.VISIBLE else View.GONE

        itemView.text_view_name.text = game.name
        itemView.text_view_name.visibility = if (game.name != null) View.VISIBLE else View.GONE

        val platform = platforms.firstOrNull { it.id == game.platform }
        itemView.text_view_platform.text = platform?.name
        itemView.text_view_platform.visibility =
            if (platform != null && sagaId == null) View.VISIBLE else View.GONE

        val rating = game.score
        itemView.rating_bar.rating = rating.toFloat() / 2
        itemView.text_view_rating.text = rating.toInt().toString()
        itemView.linear_layout_rating.visibility =
            if (rating > 0 && sagaId == null) View.VISIBLE else View.GONE
        itemView.text_view_new.visibility =
            if (rating <= 0 && sagaId == null) View.VISIBLE else View.GONE

        itemView.check_box.visibility = if (sagaId != null) View.VISIBLE else View.GONE
        itemView.check_box.isChecked = game.saga?.id == sagaId
    }
}
