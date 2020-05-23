package es.upsa.mimo.gamercollection.adapters

import android.content.Context
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
import java.util.*

class GamesAdapter(
    private val context: Context,
    var games: List<GameResponse>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private val sagaId: Int?,
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

        if (game.releaseDate != null && game.state == Constants.pending) {

            if (Date().before(game.releaseDate)) {
                item.image_view_calendar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unreleased))
            } else {
                item.image_view_calendar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.released))
            }
            item.image_view_calendar.visibility = View.VISIBLE
        } else {
            item.image_view_calendar.visibility = View.GONE
        }

        if (game.score > 0) {
            item.rating_bar.rating = (game.score / 2).toFloat()
            item.rating_bar.visibility = View.VISIBLE
        } else {
            item.rating_bar.visibility = View.GONE
        }

        item.image_view_arrow.visibility = if(sagaId != null) View.GONE else View.VISIBLE
        item.check_box.visibility = if(sagaId != null) View.VISIBLE else View.GONE
        item.check_box.isChecked = game.saga?.id  == sagaId
        item.check_box.setOnClickListener { onItemClickListener.onItemClick(game.id) }
    }

    interface OnItemClickListener {
        fun onItemClick(gameId: Int)
    }

    inner class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {

            itemView.check_box.isChecked = !itemView.check_box.isChecked
            val gameId = games[ this.adapterPosition].id
            onItemClickListener.onItemClick(gameId)
        }
    }
}