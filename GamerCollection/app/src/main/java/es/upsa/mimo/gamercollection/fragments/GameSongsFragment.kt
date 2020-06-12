package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.SongAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_game_songs.*
import kotlinx.android.synthetic.main.new_song_dialog.view.*

class GameSongsFragment : BaseFragment(), SongsAdapter.OnItemClickListener {

    private var currentGame: GameResponse? = null
    private var enable = false
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var gameRepository: GameRepository
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var songAPIClient: SongAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentGame = Gson().fromJson(arguments?.getString("game"), GameResponse::class.java)
        return inflater.inflate(R.layout.fragment_game_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        gameRepository = GameRepository(requireContext())
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        songAPIClient = SongAPIClient(resources, sharedPrefHandler)

        initializeUI()
    }

    override fun onItemClick(songId: Int) {

        currentGame?.let { game ->

            showLoading()
            songAPIClient.deleteSong(game.id, songId, {
                updateData()
            }, {
                manageError(it)
            })
        }
    }

    fun enableEdition(enable: Boolean) {

        this.enable = enable
        val adapter = recycler_view_songs?.adapter as? SongsAdapter
        if (adapter != null) {
            adapter.editable = enable
            adapter.notifyDataSetChanged()
        }
        button_add_song?.visibility = if(enable) View.VISIBLE else View.GONE
    }

    fun getSongs(): List<SongResponse> {
        return currentGame?.songs ?: ArrayList()
    }

    // MARK: Private functions

    private fun initializeUI() {

        recycler_view_songs.layoutManager = LinearLayoutManager(requireContext())
        val songs = currentGame?.songs ?: ArrayList()
        if (songs.isNotEmpty()) {
            recycler_view_songs.adapter = SongsAdapter(songs, enable, this)
        }
        recycler_view_songs.visibility = if (songs.isNotEmpty()) View.VISIBLE else View.GONE
        layout_empty_list.visibility = if (songs.isNotEmpty()) View.GONE else View.VISIBLE
        button_add_song.setOnClickListener { showNewSongPopup() }
    }

    private fun showNewSongPopup() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.new_song_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val name = dialogView.edit_text_name.text.toString()
            val singer = dialogView.edit_text_singer.text.toString()
            val url = dialogView.edit_text_url.text.toString()
            val song = SongResponse(0, name, singer, url)
            addSong(song)
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun addSong(song: SongResponse) {

        currentGame?.let { game ->

            showLoading()
            songAPIClient.createSong(game.id, song, {
                updateData()
            }, {
                manageError(it)
            })
        }
    }

    private fun updateData() {

        currentGame?.let { game ->
            gameAPIClient.getGame(game.id, {
                gameRepository.updateGame(it)

                currentGame = it
                showSongs(it)
                hideLoading()
            }, {
                manageError(it)
            })
        }
    }

    private fun showSongs(game: GameResponse?) {

        val songs = game?.songs ?: ArrayList()
        val adapter = recycler_view_songs?.adapter as? SongsAdapter
        if (adapter != null) {
            adapter.songs = songs
            adapter.notifyDataSetChanged()
        }
        recycler_view_songs.visibility = if (songs.isNotEmpty()) View.VISIBLE else View.GONE
        layout_empty_list.visibility = if (songs.isNotEmpty()) View.GONE else View.VISIBLE
    }
}