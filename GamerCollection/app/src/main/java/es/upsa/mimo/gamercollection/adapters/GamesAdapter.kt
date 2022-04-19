package es.upsa.mimo.gamercollection.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.ItemGameBinding
import es.upsa.mimo.gamercollection.databinding.ItemGameVerticalDisplayBinding
import es.upsa.mimo.gamercollection.databinding.ItemLoadMoreItemsBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.viewholders.GamesViewHolder
import es.upsa.mimo.gamercollection.viewholders.LoadMoreItemsViewHolder

class GamesAdapter(
    private var games: List<GameResponse>,
    private val isVerticalDisplay: Boolean,
    private val sagaId: Int?,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    //region Lifecycle methods
    override fun getItemViewType(position: Int): Int {

        val game = games[position]
        return when {
            isVerticalDisplay && game.id > 0 -> R.layout.item_game_vertical_display
            !isVerticalDisplay && game.id > 0 -> R.layout.item_game
            else -> R.layout.item_load_more_items
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            R.layout.item_game_vertical_display -> {
                GamesViewHolder(
                    ItemGameVerticalDisplayBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.item_game -> {
                GamesViewHolder(
                    ItemGameBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                LoadMoreItemsViewHolder(
                    ItemLoadMoreItemsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is GamesViewHolder -> holder.bind(games[position], sagaId, onItemClickListener, false)
            else -> (holder as LoadMoreItemsViewHolder).bind(onItemClickListener)
        }
    }
    //endregion

    //region Public methods
    @SuppressLint("NotifyDataSetChanged")
    fun setGames(newGames: List<GameResponse>) {

        this.games = newGames
        notifyDataSetChanged()
    }

    fun addGames(newGames: List<GameResponse>) {

        val position = games.size
        games = newGames
        notifyItemInserted(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetList() {

        games = ArrayList()
        notifyDataSetChanged()
    }
    //endregion
}