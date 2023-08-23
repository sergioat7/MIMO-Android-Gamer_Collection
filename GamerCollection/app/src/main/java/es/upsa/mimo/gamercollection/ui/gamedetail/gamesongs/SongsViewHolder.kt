package es.upsa.mimo.gamercollection.ui.gamedetail.gamesongs

import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener
import es.upsa.mimo.gamercollection.databinding.ItemSongBinding
import es.upsa.mimo.gamercollection.models.SongResponse

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