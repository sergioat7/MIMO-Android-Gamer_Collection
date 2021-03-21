package es.upsa.mimo.gamercollection.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import kotlinx.android.synthetic.main.layout_load_more_items.view.*

class LoadMoreItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // MARK: - Public methods

    fun fillData(onItemClickListener: OnItemClickListener) {
        itemView.button_load_more_items.setOnClickListener {
            onItemClickListener.onLoadMoreItemsClick()
        }
    }
}