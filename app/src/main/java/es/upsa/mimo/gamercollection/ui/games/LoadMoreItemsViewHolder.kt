package es.upsa.mimo.gamercollection.ui.games

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemLoadMoreItemsBinding

class LoadMoreItemsViewHolder(val binding: ItemLoadMoreItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(onItemClickListener: OnItemClickListener) {
        binding.onItemClickListener = onItemClickListener
    }
    //endregion
}