package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSongsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSongsViewModel
import kotlinx.android.synthetic.main.fragment_game_songs.*
import kotlinx.android.synthetic.main.new_song_dialog.view.*

class GameSongsFragment(
    private var game: GameResponse?,
    private var enabled: Boolean
) : BaseFragment(), SongsAdapter.OnItemClickListener {

    //MARK: - Private properties

    private lateinit var viewModel: GameSongsViewModel
    private lateinit var songsAdapter: SongsAdapter

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    // MARK: - Interface methods

    override fun onItemClick(songId: Int) {
        viewModel.deleteSong(songId)
    }

    // MARK: Public methods

    fun enableEdition(enable: Boolean) {

        songsAdapter.setEditable(enable)
        button_add_song?.visibility = if(enable) View.VISIBLE else View.GONE
    }

    fun getSongs(): List<SongResponse> {
        return viewModel.songs.value ?: ArrayList()
    }

    // MARK: Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, GameSongsViewModelFactory(application, game)).get(GameSongsViewModel::class.java)
        setupBindings()

        recycler_view_songs.layoutManager = LinearLayoutManager(requireContext())
        songsAdapter = SongsAdapter(ArrayList(), enabled, this)
        recycler_view_songs.adapter = songsAdapter

        button_add_song.setOnClickListener { showNewSongPopup() }
        button_add_song?.visibility = if(enabled) View.VISIBLE else View.GONE
    }

    private fun setupBindings() {

        viewModel.gameSongsLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.gameSongsError.observe(viewLifecycleOwner, { error ->
            manageError(error)
        })

        viewModel.songs.observe(viewLifecycleOwner, {

            songsAdapter.setSongs(it)
            recycler_view_songs.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            layout_empty_list.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
        })
    }

    private fun showNewSongPopup() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.new_song_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val name = dialogView.edit_text_name.text.toString()
            val singer = dialogView.edit_text_singer.text.toString()
            val url = dialogView.edit_text_url.text.toString()

            if (name.isNotBlank() || singer.isNotBlank() || url.isNotBlank()) {
                val song = SongResponse(
                    0,
                    name,
                    singer,
                    url
                )
                viewModel.createSong(song)
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }
}