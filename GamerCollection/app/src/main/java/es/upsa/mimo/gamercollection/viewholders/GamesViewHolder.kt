package es.upsa.mimo.gamercollection.viewholders

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemGameVerticalDisplayBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.State

class GamesViewHolder(
    private val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(
        game: GameResponse,
        sagaId: Int?,
        onItemClickListener: OnItemClickListener,
        isSubItem: Boolean
    ) {
        binding.apply {
            when (this) {


                is ItemGameVerticalDisplayBinding -> {
                    val color = game.state?.let {
                        when (it) {
                            State.PENDING ->
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.colorPending
                                )

                            State.IN_PROGRESS ->
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.colorInProgress
                                )

                            State.FINISHED ->
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.colorFinished
                                )

                            else -> Color.TRANSPARENT
                        }
                    } ?: run {
                        Color.TRANSPARENT
                    }
                    this.viewState.setBackgroundColor(color)
                    this.platform = Constants.PLATFORMS.firstOrNull { it.id == game.platform }
                    this.textViewRating.text = game.score.toInt().toString()
                    this.checkBox.isChecked = game.saga?.id == sagaId
                    this.game = game
                    this.isSagaIdNull = sagaId == null
                    this.onItemClickListener = onItemClickListener

                    this.root.setOnClickListener {
                        this.checkBox.isChecked = !this.checkBox.isChecked
                        if (isSubItem) {
                            onItemClickListener.onSubItemClick(game.id)
                        } else {
                            onItemClickListener.onItemClick(game.id)
                        }
                    }
                }
                else -> Unit
            }
        }
    }
    //endregion
}
