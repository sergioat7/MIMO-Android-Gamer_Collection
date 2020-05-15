package es.upsa.mimo.gamercollection.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.game_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class GamesAdapter(
    private val context: Context,
    private val resources: Resources,
    private val games: List<GameResponse>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<GamesAdapter.GamesViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GamesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {

        val game = games[position]
        val item = holder.itemView

        val state = states.firstOrNull { it.id == game.state }
        state?.let {
            when (it.id) {
                Constants.pending -> item.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color4))
                Constants.inProgress -> item.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color5))
                Constants.finished -> item.view_state.setBackgroundColor(ContextCompat.getColor(context, R.color.color3))
                else -> item.view_state.setBackgroundColor(Color.TRANSPARENT)
            }
        } ?: run {
            item.view_state.setBackgroundColor(Color.TRANSPARENT)
        }

        game.imageUrl?.let { url ->
            Picasso.with(context).load(url).into(item.image_view_game)
        } ?: run {
            item.image_view_game.setImageDrawable(null)
        }

        item.image_view_goty.visibility = if (game.goty) View.VISIBLE else View.GONE

        game.name?.let {
            item.text_view_name.text = it
            item.text_view_name.visibility = View.VISIBLE
        } ?: run {
            item.text_view_name.visibility = View.GONE
        }

        val platform = platforms.firstOrNull { it.id == game.platform }
        platform?.let {
            item.text_view_platform.text = it.name
            item.text_view_platform.visibility = View.VISIBLE
        } ?: run {
            item.text_view_platform.visibility = View.GONE
        }

        if (game.releaseDate != null && game.releaseDate.isNotEmpty() && game.state == Constants.pending) {

            val format = if (Locale.getDefault().language == "es") "d-M-y" else "M-d-y"
            val releaseDate = SimpleDateFormat(format).parse(game.releaseDate)
            if (Date().before(releaseDate)) {
                item.text_view_released.text = resources.getString(R.string.GAMES_UNRELEASED)
                item.text_view_released.setTextColor(ContextCompat.getColor(context, R.color.color4))
            } else {
                item.text_view_released.text = resources.getString(R.string.GAMES_RELEASED)
                item.text_view_released.setTextColor(ContextCompat.getColor(context, R.color.color3))
            }
            item.text_view_released.visibility = View.VISIBLE
        } else {
            item.text_view_released.visibility = View.GONE
        }

        item.rating_bar.rating = (game.score / 2).toFloat()
    }

    interface OnItemClickListener {
        fun onItemClick(gameId: Int)
    }

    inner class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            val gameId = games[ this.adapterPosition].id
            onItemClickListener.onItemClick(gameId)
        }
    }
}