package es.upsa.mimo.gamercollection.ui.gamedetail.gamesongs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.databinding.ItemSongBinding
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener

class SongsAdapter(
    private var songs: List<SongResponse>,
    private var editable: Boolean,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SongsViewHolder?>() {

    //region Lifecycle methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        return SongsViewHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bind(songs[position], editable, onItemClickListener)
    }
    //endregion

    //region Public methods
    @SuppressLint("NotifyDataSetChanged")
    fun setSongs(newSongs: List<SongResponse>) {

        this.songs = newSongs
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEditable(editable: Boolean) {

        this.editable = editable
        notifyDataSetChanged()
    }
    //endregion
}