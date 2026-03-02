package es.upsa.mimo.gamercollection.presentation.games

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.databinding.ItemLoadMoreItemsBinding
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener

class LoadMoreItemsViewHolder(val binding: ItemLoadMoreItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(onItemClickListener: OnItemClickListener) {
        binding.onItemClickListener = onItemClickListener
    }
    //endregion
}