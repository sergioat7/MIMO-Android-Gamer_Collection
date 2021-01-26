package es.upsa.mimo.gamercollection.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.models.base.BaseModel
import es.upsa.mimo.gamercollection.viewholders.GamesViewHolder
import es.upsa.mimo.gamercollection.viewholders.SagasViewHolder
import kotlinx.android.synthetic.main.game_item.view.*
import kotlinx.android.synthetic.main.saga_item.view.*
import kotlinx.android.synthetic.main.saga_item.view.image_view_arrow

class SagasAdapter(
    private var items: MutableList<BaseModel<Int>>,
    private var expandedIds: MutableList<Int>,
    private val platforms: List<PlatformResponse>,
    private val states: List<StateResponse>,
    private val context: Context,
    private var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<ViewHolder?>() {

    //MARK: - Lifecycle methods

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        var holder: ViewHolder? = null
        when (viewType) {
            R.layout.saga_item -> holder = SagasViewHolder(itemView)
            R.layout.game_item -> holder = GamesViewHolder(itemView, platforms, states)
        }
        return holder!!
    }

    override fun getItemViewType(position: Int): Int {

        return when(items[position]) {
            is SagaResponse -> R.layout.saga_item
            is GameResponse -> R.layout.game_item
            else -> throw Throwable("Unsupported type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder is SagasViewHolder) {

            val saga = items[position] as SagaResponse
            holder.fillData(saga, context)

            val rotation = if (expandedIds.contains(saga.id)) 0f else -180f
            holder.rotateArrow(rotation)

            holder.itemView.image_view_arrow.setOnClickListener {
                if (expandedIds.contains(saga.id)) {

                    val currentPosition = holder.layoutPosition
                    holder.rotateArrow(-180f)
                    expandedIds.remove(saga.id)
                    items.removeAll(saga.games)
                    notifyItemRangeRemoved(currentPosition+1, saga.games.size)
                } else {

                    val currentPosition = holder.layoutPosition
                    holder.rotateArrow(0f)
                    expandedIds.add(saga.id)
                    if (currentPosition+1 < items.size) {
                        items.addAll(currentPosition+1, saga.games.sortedBy { it.releaseDate })
                    } else {
                        items.addAll(saga.games.sortedBy { it.releaseDate })
                    }
                    notifyItemRangeInserted(currentPosition+1, saga.games.size)
                }
            }

            holder.itemView.image_view_edit.setOnClickListener {
                onItemClickListener.onItemClick(saga.id)
            }

        } else if (holder is GamesViewHolder) {

            val game = items[position] as GameResponse
            holder.fillData(game, context, null)

            holder.itemView.check_box.setOnClickListener {
                onItemClickListener.onSubItemClick(game.id)
            }

            holder.itemView.setOnClickListener {
                holder.itemView.check_box.isChecked = !holder.itemView.check_box.isChecked
                onItemClickListener.onSubItemClick(game.id)
            }
        }
    }

    //MARK: - Public methods

    fun setItems(newItems: MutableList<BaseModel<Int>>) {
        this.items = newItems
    }

    fun setExpandedIds(newExpandedIds: MutableList<Int>) {
        this.expandedIds = newExpandedIds
    }

    fun resetList() {

        this.items = mutableListOf()
        notifyDataSetChanged()
    }
}