package es.upsa.mimo.gamercollection.adapters

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import kotlinx.android.synthetic.main.saga_item.view.*

class SagasAdapter(
    private val context: Context,
    var sagas: List<SagaResponse>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<SagasAdapter.SagasViewHolder?>(), GamesAdapter.OnItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SagasViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.saga_item, parent, false)
        return SagasViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return sagas.size
    }

    override fun onBindViewHolder(holder: SagasViewHolder, position: Int) {

        val saga = sagas[position]
        val item = holder.itemView

        item.edit_text_name.setText(saga.name)
        item.edit_text_name.setReadOnly(true, InputType.TYPE_NULL, 0)
        item.edit_text_name.setOnClickListener { holder.onClick(it) }

        item.image_view_edit.setOnClickListener {
            val sagaId = saga.id
            onItemClickListener.onItemClick(sagaId)
        }

        item.recycler_view_games.layoutManager = LinearLayoutManager(context)
        val games = saga.games.sortedBy { it.releaseDate }
        item.recycler_view_games.adapter = GamesAdapter(context, games, platforms, states, null, this)
        item.recycler_view_games.visibility = if(saga.games.isEmpty()) View.GONE else View.VISIBLE
    }

    interface OnItemClickListener {
        fun onItemClick(sagaId: Int)
        fun onGameItemClick(gameId: Int)
    }

    inner class SagasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            val recyclerView = itemView.recycler_view_games
            recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onItemClick(gameId: Int) {
        onItemClickListener.onGameItemClick(gameId)
    }
}