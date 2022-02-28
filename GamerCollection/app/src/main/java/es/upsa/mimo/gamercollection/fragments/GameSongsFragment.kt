package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameSongsBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSongsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSongsViewModel
import kotlinx.android.synthetic.main.new_song_dialog.view.*

class GameSongsFragment(
    private var game: GameResponse?,
    private var enabled: Boolean
) : BindingFragment<FragmentGameSongsBinding>(), OnItemClickListener {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region Private properties
    private lateinit var viewModel: GameSongsViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {
        viewModel.deleteSong(id)
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {
    }
    //endregion

    //region Public methods
    fun setEdition(editable: Boolean) {
        binding.editable = editable
    }

    fun getSongs(): List<SongResponse> {
        return viewModel.songs.value ?: ArrayList()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GameSongsViewModelFactory(application, game)
        )[GameSongsViewModel::class.java]
        setupBindings()

        with(binding) {

            recyclerViewSongs.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = SongsAdapter(
                    listOf(),
                    enabled,
                    this@GameSongsFragment
                )
            }

            buttonAddSong.setOnClickListener {
                showNewSongPopup()
            }

            viewModel = this@GameSongsFragment.viewModel
            lifecycleOwner = this@GameSongsFragment
            editable = enabled
        }
    }

    private fun setupBindings() {

        viewModel.gameSongsLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gameSongsError.observe(viewLifecycleOwner) { error ->
            manageError(error)
        }
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
    //endregion
}