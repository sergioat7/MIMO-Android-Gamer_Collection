package es.upsa.mimo.gamercollection.viewholders

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.GameItemBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.State

class GamesViewHolder(
    private val binding: GameItemBinding,
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

        val image = game.imageUrl ?: Constants.NO_VALUE
        val errorImage =
            if (Constants.isDarkMode(binding.root.context)) R.drawable.ic_default_game_cover_dark
            else R.drawable.ic_default_game_cover_light
        val loading = binding.progressBarLoading
        loading.visibility = View.VISIBLE
        Picasso
            .get()
            .load(image)
            .fit()
            .centerCrop()
            .error(errorImage)
            .into(binding.imageViewGame, object : Callback {

                override fun onSuccess() {
                    loading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    loading.visibility = View.GONE
                }
            })

        val rating = game.score

        with(binding) {

            viewState.setBackgroundColor(color)

            platform = platforms.firstOrNull { it.id == game.platform }

            textViewRating.text = rating.toInt().toString()

            checkBox.isChecked = game.saga?.id == sagaId

            this.game = game
            isSagaIdNull = sagaId == null
            this.onItemClickListener = onItemClickListener
        }
    }
    //endregion
}
