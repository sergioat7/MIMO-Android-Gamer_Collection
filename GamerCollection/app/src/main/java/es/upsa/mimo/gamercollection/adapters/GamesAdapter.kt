package es.upsa.mimo.gamercollection.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.viewholders.GamesViewHolder
import es.upsa.mimo.gamercollection.viewholders.LoadMoreItemsViewHolder
import kotlinx.android.synthetic.main.game_item.view.*
import java.util.*

class GamesAdapter(
    private var games: List<GameResponse>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private val sagaId: Int?,
    private val context: Context,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    //MARK: - Lifecycle methods

    override fun getItemViewType(position: Int): Int {

        return if (games[position].id > 0) {
            R.layout.game_item
        } else {
            R.layout.layout_load_more_items
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(
            viewType,
            parent,
            false
        )
        return if (viewType == R.layout.game_item) {
            GamesViewHolder(itemView, platforms, states)
        } else {
            LoadMoreItemsViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is GamesViewHolder) {

            val game = games[position]
            holder.fillData(game, context, sagaId)
            holder.itemView.check_box.setOnClickListener {
                onItemClickListener.onItemClick(game.id)
            }
            holder.itemView.setOnClickListener {
                holder.itemView.check_box.isChecked = !holder.itemView.check_box.isChecked
                onItemClickListener.onItemClick(game.id)
            }
        } else {

            val loadMoreItemsViewHolder = holder as LoadMoreItemsViewHolder
            loadMoreItemsViewHolder.fillData(onItemClickListener)
        }
    }

    //MARK: - Public methods

    fun setGames(newGames: List<GameResponse>) {

        this.games = newGames
        notifyDataSetChanged()
    }

    fun addGames(newGames: List<GameResponse>) {

        val position = games.size
        games = newGames
        notifyItemInserted(position)
    }

    fun resetList() {

        games = ArrayList()
        notifyDataSetChanged()
    }
}