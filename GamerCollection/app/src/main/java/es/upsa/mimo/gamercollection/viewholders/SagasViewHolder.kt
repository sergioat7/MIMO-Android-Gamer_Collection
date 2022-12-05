package es.upsa.mimo.gamercollection.viewholders

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemSagaBinding
import es.upsa.mimo.gamercollection.extensions.isDarkMode
import es.upsa.mimo.gamercollection.models.responses.SagaResponse

class SagasViewHolder(val binding: ItemSagaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(saga: SagaResponse, onItemClickListener: OnItemClickListener) {
        with(binding) {

            this.saga = saga
            this.onItemClickListener = onItemClickListener
            this.isDarkMode = binding.root.context.isDarkMode()
        }

    }

    fun rotateArrow(value: Float) {
        binding.imageViewArrow.animate().setDuration(500).rotation(value).start()
    }
    //endregion
}