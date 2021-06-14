package es.upsa.mimo.gamercollection.viewholders

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.LayoutLoadMoreItemsBinding

class LoadMoreItemsViewHolder(val binding: LayoutLoadMoreItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(onItemClickListener: OnItemClickListener) {
        binding.onItemClickListener = onItemClickListener
    }
    //endregion
}