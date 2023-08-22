package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.DialogGamesBinding
import es.upsa.mimo.gamercollection.databinding.FragmentSagaDetailBinding
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.isDarkMode
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodels.SagaDetailViewModel

@AndroidEntryPoint
class SagaDetailFragment : BindingFragment<FragmentSagaDetailBinding>(), OnItemClickListener {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private val viewModel: SagaDetailViewModel by viewModels()
    private var menu: Menu? = null
    private var newGames: MutableList<GameResponse> = mutableListOf()
    private val goBack = MutableLiveData<Boolean>()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        initializeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.saga_toolbar_menu, menu)
        menu.findItem(R.id.action_edit).isVisible = viewModel.saga.value != null
        menu.findItem(R.id.action_remove).isVisible = viewModel.saga.value != null
        menu.findItem(R.id.action_save).isVisible = viewModel.saga.value == null
        menu.findItem(R.id.action_cancel).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_edit -> {

                editSaga()
                return true
            }

            R.id.action_remove -> {

                showPopupConfirmationDialog(
                    resources.getString(R.string.saga_detail_delete_confirmation),
                    {
                        viewModel.deleteSaga()
                    })
                return true
            }

            R.id.action_save -> {

                viewModel.saveSaga(binding.textInputLayoutSagaName.getValue(), newGames)
                return true
            }

            R.id.action_cancel -> {

                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {

        val selectedGame = viewModel.games.firstOrNull { it.id == id }
        newGames.firstOrNull { it.id == id }?.let {

            newGames.remove(it)
            it.saga = null
        } ?: run {
            selectedGame?.let {

                newGames.add(it)
                it.saga = viewModel.saga.value
            }
        }
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {
    }
    //endregion

    //region Public methods
    fun addGame() {

        val dialogBinding = DialogGamesBinding.inflate(layoutInflater)

        val orderedGames = viewModel.getOrderedGames(viewModel.games).map { game ->
            if (newGames.firstOrNull { it.id == game.id } != null) {
                game.saga = viewModel.saga.value
            } else {
                game.saga = null
            }
            game
        }
        if (orderedGames.isNotEmpty()) {

            dialogBinding.recyclerViewGames.adapter = GamesAdapter(
                orderedGames,
                viewModel.saga.value?.id ?: 0,
                this
            )
        }
        dialogBinding.recyclerViewGames.visibility =
            if (orderedGames.isNotEmpty()) View.VISIBLE else View.GONE
        dialogBinding.layoutEmptyList.root.visibility =
            if (orderedGames.isNotEmpty()) View.GONE else View.VISIBLE

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                showGames(newGames)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        setupBindings()

        binding.addGamesEnabled = true

        binding.fragment = this
        binding.isDarkMode = context.isDarkMode()

        newGames = viewModel.saga.value?.games?.toMutableList() ?: mutableListOf()
    }

    private fun setupBindings() {

        viewModel.sagaDetailLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {

                hideLoading()
                cancelEdition()
            }
        }

        viewModel.sagaDetailSuccessMessage.observe(viewLifecycleOwner) {

            val message = resources.getString(it)
            showPopupDialog(message, goBack)
        }

        viewModel.sagaDetailError.observe(viewLifecycleOwner) { error ->

            hideLoading()
            error?.let {
                manageError(it)
            }
        }

        viewModel.saga.observe(viewLifecycleOwner) { saga ->

            showData(saga)
            enableEdition(saga == null)
        }

        goBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }
    //endregion

    //region Private methods
    private fun showData(saga: SagaResponse?) {

        saga?.let {

            binding.sagaName = it.name
            showGames(it.games)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showGames(games: List<GameResponse>) {

        binding.linearLayoutGames.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 15, 0, 15)

        for (game in games.sortedBy { it.releaseDate }) {

            val tvGame = TextView(requireContext())
            tvGame.setTextAppearance(R.style.Widget_GamerCollection_TextView_Description_SagaGame)
            tvGame.text = "- ${game.name}"
            binding.linearLayoutGames.addView(tvGame, layoutParams)
        }
    }

    private fun enableEdition(enable: Boolean) {
        binding.editable = enable
    }

    private fun editSaga() {

        showEditButton(true)
        enableEdition(true)
    }

    private fun cancelEdition() {

        showEditButton(false)
        showData(viewModel.saga.value)
        enableEdition(false)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_remove).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }
    //endregion
}
