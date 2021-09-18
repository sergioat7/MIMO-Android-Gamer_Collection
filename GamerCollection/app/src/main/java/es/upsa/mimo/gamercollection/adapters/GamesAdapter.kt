package es.upsa.mimo.gamercollection.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.GameItemBinding
import es.upsa.mimo.gamercollection.databinding.LayoutLoadMoreItemsBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.viewholders.GamesViewHolder
import es.upsa.mimo.gamercollection.viewholders.LoadMoreItemsViewHolder
import kotlinx.android.synthetic.main.game_item.view.*
import java.util.*

class GamesAdapter(
    private var games: List<GameResponse>,
    private val platforms: List<PlatformResponse>,
    private val sagaId: Int?,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    //region Lifecycle methods
    override fun getItemViewType(position: Int): Int {

        return if (games[position].id > 0) {
            R.layout.game_item
        } else {
            R.layout.layout_load_more_items
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == R.layout.game_item) {
            GamesViewHolder(
                GameItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                platforms
            )
        } else {
            LoadMoreItemsViewHolder(
                LayoutLoadMoreItemsBinding.inflate(
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
                holder.itemView.check_box.isChecked = !holder.itemView.check_box.isChecked
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

@BindingAdapter("games")
fun setRecyclerViewGames(recyclerView: RecyclerView?, games: List<GameResponse>?) {

    val adapter = recyclerView?.adapter
    if (adapter is GamesAdapter && games != null) {
        adapter.setGames(games)
    }
}

@BindingAdapter("newGames")
fun addRecyclerViewGames(recyclerView: RecyclerView?, newGames: MutableList<GameResponse>?) {

    val adapter = recyclerView?.adapter
    if (adapter is GamesAdapter && newGames != null) {
        adapter.addGames(newGames)
    }
}