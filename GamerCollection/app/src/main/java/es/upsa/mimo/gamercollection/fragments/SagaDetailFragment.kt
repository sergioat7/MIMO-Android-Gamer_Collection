package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSagaDetailBinding
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.SagaDetailViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SagaDetailViewModel
import kotlinx.android.synthetic.main.games_dialog.view.*

class SagaDetailFragment : BindingFragment<FragmentSagaDetailBinding>(), OnItemClickListener {

    //region Private properties
    private lateinit var viewModel: SagaDetailViewModel
    private var menu: Menu? = null
    private var sagaGames: List<GameResponse> = arrayListOf()
    private var newGames: ArrayList<GameResponse> = arrayListOf()
    private val goBack = MutableLiveData<Boolean>()
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
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

                showPopupConfirmationDialog(resources.getString(R.string.saga_detail_delete_confirmation)) {
                    viewModel.deleteSaga()
                }
                return true
            }
            R.id.action_save -> {

                viewModel.saveSaga(binding.editTextName.text.toString(), newGames)
                return true
            }
            R.id.action_cancel -> {

                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.games_dialog, null)

        dialogView.recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val orderedGames = viewModel.getOrderedGames(viewModel.games)
        if (orderedGames.isNotEmpty()) {

            dialogView.recycler_view_games.adapter = GamesAdapter(
                orderedGames,
                viewModel.platforms,
                viewModel.saga.value?.id ?: 0,
                this
            )
        }
        dialogView.recycler_view_games.visibility =
            if (orderedGames.isNotEmpty()) View.VISIBLE else View.GONE
        dialogView.layout_empty_list.visibility =
            if (orderedGames.isNotEmpty()) View.GONE else View.VISIBLE

        dialogView.button_accept.setOnClickListener {

            showGames(newGames)
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        val sagaId = this.arguments?.getInt(Constants.SAGA_ID)
        viewModel = ViewModelProvider(this, SagaDetailViewModelFactory(application, sagaId)).get(
            SagaDetailViewModel::class.java
        )
        setupBindings()

        binding.addGamesEnabled = viewModel.saga.value != null

        binding.fragment = this
    }

    private fun setupBindings() {

        viewModel.sagaDetailLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {

                hideLoading()
                cancelEdition()
            }
        })

        viewModel.sagaDetailSuccessMessage.observe(viewLifecycleOwner, {

            val message = resources.getString(it)
            showPopupDialog(message, goBack)
        })

        viewModel.sagaDetailError.observe(viewLifecycleOwner, { error ->

            hideLoading()
            manageError(error)
        })

        viewModel.saga.observe(viewLifecycleOwner, { saga ->

            saga?.let {
                sagaGames = it.games
            }

            showData(saga)
            enableEdition(saga == null)
        })

        goBack.observe(viewLifecycleOwner, {
            activity?.finish()
        })
    }

    private fun showData(saga: SagaResponse?) {

        saga?.let {

            binding.sagaName = saga.name
            newGames.clear()
            newGames.addAll(saga.games)
            showGames(newGames)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) tvGame.setTextAppearance(R.style.Widget_GamerCollection_EditText_Regular)
            else tvGame.setTextAppearance(
                requireContext(),
                R.style.Widget_GamerCollection_EditText_Regular
            )
            tvGame.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)

            tvGame.text = "- ${game.name}"
            binding.linearLayoutGames.addView(tvGame, layoutParams)
        }
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        binding.editTextName.setReadOnly(!enable, inputTypeText, backgroundColor)
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
