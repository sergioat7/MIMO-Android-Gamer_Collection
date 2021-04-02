package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.databinding.FragmentGameSongsBinding
import es.upsa.mimo.gamercollection.fragments.base.BindingFragment
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSongsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSongsViewModel
import kotlinx.android.synthetic.main.new_song_dialog.view.*

class GameSongsFragment(
    private var game: GameResponse?,
    private var enabled: Boolean
) : BindingFragment<FragmentGameSongsBinding>(), OnItemClickListener {

    //MARK: - Private properties

    private lateinit var viewModel: GameSongsViewModel
    private lateinit var songsAdapter: SongsAdapter

    // MARK: - Lifecycle methods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    // MARK: - Interface methods

    override fun onItemClick(id: Int) {
        viewModel.deleteSong(id)
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {
    }

    // MARK: Public methods

    fun setEdition(editable: Boolean) {

        binding.editable = editable
        songsAdapter.setEditable(editable)
    }

    fun getSongs(): List<SongResponse> {
        return viewModel.songs.value ?: ArrayList()
    }

    // MARK: Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, GameSongsViewModelFactory(application, game)).get(
            GameSongsViewModel::class.java
        )
        setupBindings()

        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(requireContext())
        songsAdapter = SongsAdapter(
            ArrayList(),
            enabled,
            requireContext(),
            this
        )
        binding.recyclerViewSongs.adapter = songsAdapter

        binding.buttonAddSong.setOnClickListener {
            showNewSongPopup()
        }

        binding.editable = enabled
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
            binding.songs = it
        })
    }

    private fun showNewSongPopup() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.new_song_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val name = dialogView.custom_edit_text_name.getText()
            val singer = dialogView.custom_edit_text_singer.getText()
            val url = dialogView.custom_edit_text_url.getText()

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