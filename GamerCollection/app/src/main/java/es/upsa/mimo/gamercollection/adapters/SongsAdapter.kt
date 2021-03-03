package es.upsa.mimo.gamercollection.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.saga_item.view.*
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter(
    private var songs: List<SongResponse>,
    private var editable: Boolean,
    private val context: Context,
    private var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<SongsAdapter.SongsViewHolder?>() {

    //MARK: - Lifecycle methods

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
        val imageId = if (Constants.isDarkMode(context)) R.drawable.ic_trash_dark else R.drawable.ic_trash_light
        holder.itemView.image_view_remove.setImageDrawable(ContextCompat.getDrawable(context, imageId))
        holder.itemView.image_view_remove.visibility = if (editable) View.VISIBLE else View.GONE
        holder.itemView.image_view_remove.setOnClickListener {
            onItemClickListener.onItemClick(song.id)
        }
    }

    //MARK: - Public methods

    fun setSongs(newSongs: List<SongResponse>) {

        this.songs = newSongs
        notifyDataSetChanged()
    }

    fun setEditable(editable: Boolean) {

        this.editable = editable
        notifyDataSetChanged()
    }

    class SongsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}