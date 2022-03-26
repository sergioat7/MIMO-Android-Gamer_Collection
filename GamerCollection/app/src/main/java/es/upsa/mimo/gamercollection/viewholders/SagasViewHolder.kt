package es.upsa.mimo.gamercollection.viewholders

import android.text.InputType
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemSagaBinding
import es.upsa.mimo.gamercollection.extensions.isDarkMode
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.models.responses.SagaResponse

class SagasViewHolder(val binding: ItemSagaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(saga: SagaResponse, onItemClickListener: OnItemClickListener) {

        val gamesCount = saga.games.size
        val title = if (gamesCount > 0) {
            binding.root.context.resources.getQuantityString(
                R.plurals.saga_title,
                gamesCount,
                saga.name,
                gamesCount
            )
        } else {
            saga.name
        }

        with(binding) {

            editTextName.setText(title)
            editTextName.setReadOnly(true, InputType.TYPE_NULL, 0)
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