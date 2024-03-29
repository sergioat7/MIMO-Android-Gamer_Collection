package es.upsa.mimo.gamercollection.ui.games

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.ItemGameBinding
import es.upsa.mimo.gamercollection.databinding.ItemLoadMoreItemsBinding
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener

class GamesAdapter(
    private var games: List<GameResponse>,
    private val sagaId: Int?,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    //region Lifecycle methods
    override fun getItemViewType(position: Int): Int {

        return if (games[position].id >= 0) {
            R.layout.item_game
        } else {
            R.layout.item_load_more_items
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == R.layout.item_game) {
            GamesViewHolder(
                ItemGameBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            LoadMoreItemsViewHolder(
                ItemLoadMoreItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is GamesViewHolder) {

            val game = games[position]
            holder.bind(game, sagaId, onItemClickListener)

            holder.itemView.setOnClickListener {
                holder.binding.checkBox.isChecked = !holder.binding.checkBox.isChecked
                onItemClickListener.onItemClick(game.id)
            }
        } else {
            (holder as LoadMoreItemsViewHolder).bind(onItemClickListener)
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