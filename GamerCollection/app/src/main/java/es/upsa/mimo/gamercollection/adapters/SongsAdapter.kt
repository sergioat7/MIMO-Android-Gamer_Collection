package es.upsa.mimo.gamercollection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.SongResponse
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter(
    private val songs: List<SongResponse>,
    var editable: Boolean,
    private val removeHandler: (Int) -> Unit
): RecyclerView.Adapter<SongsAdapter.SongsViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {

        val song = songs[position]
        holder.itemView.text_view_name.text = song.name
        holder.itemView.text_view_singer.text = song.singer
        holder.itemView.text_view_url.text = song.url
        holder.itemView.button_remove.visibility = if (editable) View.VISIBLE else View.GONE
        holder.itemView.button_remove.setOnClickListener {
            removeHandler(song.id)
        }
    }

    class SongsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }
}