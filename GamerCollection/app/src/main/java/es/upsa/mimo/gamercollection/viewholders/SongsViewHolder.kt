package es.upsa.mimo.gamercollection.viewholders

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemSongBinding
import es.upsa.mimo.gamercollection.models.responses.SongResponse

class SongsViewHolder(val binding: ItemSongBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //region Public methods
    fun bind(song: SongResponse, editable: Boolean, onItemClickListener: OnItemClickListener) {
        with(binding) {

            this.song = song
            this.editable = editable
            this.onItemClickListener = onItemClickListener
            executePendingBindings()
        }
    }
    //endregion
}