package es.upsa.mimo.gamercollection.ui.gamedetail.gamesongs

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener
import es.upsa.mimo.gamercollection.ui.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.DialogNewSongBinding
import es.upsa.mimo.gamercollection.databinding.FragmentGameSongsBinding
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.data.source.GameRepository
import es.upsa.mimo.gamercollection.data.source.SongRepository
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import javax.inject.Inject

@AndroidEntryPoint
class GameSongsFragment(
    private val game: GameResponse?,
    private var enabled: Boolean
) : BindingFragment<FragmentGameSongsBinding>(), OnItemClickListener {

    //region Public properties
    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var songRepository: SongRepository
    //endregion

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = false
    //endregion

    //region Private properties
    private lateinit var viewModel: GameSongsViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
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

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        viewModel = GameSongsViewModel(game, gameRepository, songRepository)
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
    //endregion

    //region Private methods
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

        val dialogBinding = DialogNewSongBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                val name = dialogBinding.textInputLayoutSongName.getValue()
                val singer = dialogBinding.textInputLayoutSongSinger.getValue()
                val url = dialogBinding.textInputLayoutSongUrl.getValue()

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