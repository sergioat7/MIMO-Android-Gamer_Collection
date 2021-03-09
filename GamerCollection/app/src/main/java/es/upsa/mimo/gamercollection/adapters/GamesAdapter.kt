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
import kotlinx.android.synthetic.main.game_item.view.*

class GamesAdapter(
    private var games: List<GameResponse>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private val sagaId: Int?,
    private val context: Context,
    private var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<GamesViewHolder?>() {

    //MARK: - Lifecycle methods

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GamesViewHolder(itemView, platforms, states)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {

        val game = games[position]

        holder.fillData(game, context, sagaId)
        holder.itemView.check_box.setOnClickListener { onItemClickListener.onItemClick(game.id) }

        holder.itemView.setOnClickListener {
            holder.itemView.check_box.isChecked = !holder.itemView.check_box.isChecked
            onItemClickListener.onItemClick(game.id)
        }
    }

    //MARK: - Public methods

    fun setGames(newGames: List<GameResponse>) {

        this.games = newGames
        notifyDataSetChanged()
    }
}