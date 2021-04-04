package es.upsa.mimo.gamercollection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.databinding.SongItemBinding
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.utils.Constants

class SongsAdapter(
    private var songs: List<SongResponse>,
    private var editable: Boolean,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SongsAdapter.SongsViewHolder?>() {

    //region Lifecycle methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        return SongsViewHolder(
            SongItemBinding.inflate(
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
        holder.bind(songs[position])
    }
    //endregion

    //region Public methods
    fun setSongs(newSongs: List<SongResponse>) {

        this.songs = newSongs
        notifyDataSetChanged()
    }

    fun setEditable(editable: Boolean) {

        this.editable = editable
        notifyDataSetChanged()
    }
    //endregion

    inner class SongsViewHolder(val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(_song: SongResponse) {
            with(binding) {

                darkMode = Constants.isDarkMode(root.context)
                song = _song
                editable = this@SongsAdapter.editable
                onItemClickListener = this@SongsAdapter.onItemClickListener
                executePendingBindings()
            }
        }
    }
}

@BindingAdapter("songs")
fun setRecyclerViewSongs(recyclerView: RecyclerView?, songs: List<SongResponse>?) {

    val adapter = recyclerView?.adapter
    if (adapter is SongsAdapter && songs != null) {
        adapter.setSongs(songs)
    }
}

@BindingAdapter("editable")
fun setRecyclerViewEditable(recyclerView: RecyclerView?, editable: Boolean?) {

    val adapter = recyclerView?.adapter
    if (adapter is SongsAdapter && editable != null) {
        adapter.setEditable(editable)
    }
}