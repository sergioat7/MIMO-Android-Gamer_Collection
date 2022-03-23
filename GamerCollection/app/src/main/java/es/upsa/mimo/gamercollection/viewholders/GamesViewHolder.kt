package es.upsa.mimo.gamercollection.viewholders

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemGameBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.utils.State

class GamesViewHolder(
    val binding: ItemGameBinding,
    private val platforms: List<PlatformResponse>
) : RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(game: GameResponse, sagaId: Int?, onItemClickListener: OnItemClickListener) {

        val color = game.state?.let {
            when (it) {
                State.PENDING_STATE ->
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorPending
                    )

                State.IN_PROGRESS_STATE ->
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorInProgress
                    )

                State.FINISHED_STATE ->
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorFinished
                    )

                else -> Color.TRANSPARENT
            }
        } ?: run {
            Color.TRANSPARENT
        }

        with(binding) {

            viewState.setBackgroundColor(color)
            platform = platforms.firstOrNull { it.id == game.platform }
            textViewRating.text = game.score.toInt().toString()
            checkBox.isChecked = game.saga?.id == sagaId
            this.game = game
            isSagaIdNull = sagaId == null
            this.onItemClickListener = onItemClickListener
        }
    }
    //endregion
}
