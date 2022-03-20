package es.upsa.mimo.gamercollection.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseModel
import es.upsa.mimo.gamercollection.databinding.ItemGameBinding
import es.upsa.mimo.gamercollection.databinding.ItemSagaBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewholders.GamesViewHolder
import es.upsa.mimo.gamercollection.viewholders.SagasViewHolder

class SagasAdapter(
    private var items: MutableList<BaseModel<Int>>,
    private var expandedIds: MutableList<Int>,
    private val platforms: List<PlatformResponse>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ViewHolder?>() {

    //region Lifecycle methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            R.layout.item_saga -> SagasViewHolder(
                ItemSagaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_game -> GamesViewHolder(
                ItemGameBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                platforms
            )
            else -> throw Throwable("Unsupported type")
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (items[position]) {
            is SagaResponse -> R.layout.item_saga
            is GameResponse -> R.layout.item_game
            else -> throw Throwable("Unsupported type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder is SagasViewHolder) {

            val saga = items[position] as SagaResponse
            holder.bind(saga, onItemClickListener)

            val rotation =
                if (expandedIds.contains(saga.id)) Constants.POINT_DOWN
                else Constants.POINT_UP
            holder.rotateArrow(rotation)

            holder.binding.imageViewArrow.setOnClickListener {
                if (expandedIds.contains(saga.id)) {

                    val currentPosition = holder.layoutPosition
                    holder.rotateArrow(Constants.POINT_UP)
                    expandedIds.remove(saga.id)
                    items.removeAll(saga.games)
                    notifyItemRangeRemoved(currentPosition + 1, saga.games.size)
                } else {

                    val currentPosition = holder.layoutPosition
                    holder.rotateArrow(Constants.POINT_DOWN)
                    expandedIds.add(saga.id)
                    if (currentPosition + 1 < items.size) {
                        items.addAll(currentPosition + 1, saga.games.sortedBy { it.releaseDate })
                    } else {
                        items.addAll(saga.games.sortedBy { it.releaseDate })
                    }
                    notifyItemRangeInserted(currentPosition + 1, saga.games.size)
                }
            }

        } else if (holder is GamesViewHolder) {

            val game = items[position] as GameResponse
            holder.bind(game, null, onItemClickListener)

            holder.itemView.setOnClickListener {
                holder.binding.checkBox.isChecked = !holder.binding.checkBox.isChecked
                onItemClickListener.onSubItemClick(game.id)
            }
        }
    }
    //endregion

    //region Public methods
    fun setItems(newItems: MutableList<BaseModel<Int>>) {
        this.items = newItems
    }

    fun getExpandedIds(): MutableList<Int> {
        return this.expandedIds
    }

    fun setExpandedIds(newExpandedIds: MutableList<Int>) {
        this.expandedIds = newExpandedIds
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetList() {

        this.items = mutableListOf()
        notifyDataSetChanged()
    }
    //endregion
}