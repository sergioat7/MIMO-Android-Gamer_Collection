package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameSongsBinding
import es.upsa.mimo.gamercollection.databinding.NewSongDialogBinding
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSongsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSongsViewModel

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

        val dialogBinding = NewSongDialogBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                val name = dialogBinding.customEditTextName.getText()
                val singer = dialogBinding.customEditTextSinger.getText()
                val url = dialogBinding.customEditTextUrl.getText()

                if (name.isNotBlank() || singer.isNotBlank() || url.isNotBlank()) {
                    val song = SongResponse(
                        0,
                        name,
                        singer,
                        url
                    )
                    viewModel.createSong(song)
                }
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    //endregion
}