package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.SagaRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_saga_detail.*
import kotlinx.android.synthetic.main.games_dialog.view.*

class SagaDetailFragment : BaseFragment(), GamesAdapter.OnItemClickListener {

    private var sagaId: Int? = null
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var gameRepository: GameRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository
    private lateinit var sagaRepository: SagaRepository
    private lateinit var sagaAPIClient: SagaAPIClient
    private var menu: Menu? = null
    private var currentSaga: SagaResponse? = null
    private var sagaGames: List<GameResponse> = arrayListOf()
    private var newGames: List<GameResponse> = arrayListOf()
    private var allGames: List<GameResponse> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sagaId = this.arguments?.getInt("sagaId")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_saga_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        gameRepository = GameRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())
        sagaRepository = SagaRepository(requireContext())
        sagaAPIClient = SagaAPIClient(resources, sharedPrefHandler)

        initializeUI()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.saga_toolbar_menu, menu)
        menu.findItem(R.id.action_edit).isVisible = currentSaga != null
        menu.findItem(R.id.action_save).isVisible = currentSaga == null
        menu.findItem(R.id.action_cancel).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit -> {
                editSaga()
                return true
            }
            R.id.action_save -> {
                saveSaga()
                return true
            }
            R.id.action_cancel -> {
                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(gameId: Int) {

        val selectedGame = allGames.firstOrNull { it.id == gameId }
        newGames.firstOrNull { it.id == gameId }?.let {

            newGames -= it
            selectedGame?.saga = null
        } ?: run {
            selectedGame?.let {

                newGames += it
                it.saga = currentSaga
            }
        }
    }

    //MARK: - Private functions

    private fun initializeUI() {

        button_add_game.setOnClickListener { addGame() }
        button_delete_saga.setOnClickListener { deleteSaga() }
    }

    private fun loadData() {

        sagaId?.let {

            showLoading()
            currentSaga = sagaRepository.getSaga(it)
            currentSaga?.let { saga ->
                sagaGames = saga.games
                newGames = sagaGames
            }
            hideLoading()
        }

        showData(currentSaga)
        enableEdition(currentSaga == null)
    }

    private fun showData(saga: SagaResponse?) {

        allGames = gameRepository.getGames()
        currentSaga = saga
        saga?.let { saga ->

            edit_text_name.setText(saga.name)
            newGames = saga.games
            showGames(newGames)
        }
    }

    private fun showGames(games: List<GameResponse>) {

        linear_layout_games.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 15, 0, 15)

        val orderedGames = Constants.orderGamesBy(games, sharedPrefHandler.getSortingKey())
        for (game in orderedGames) {

            val tvGame = TextView(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvGame.setTextAppearance(R.style.WhiteEditText_Regular)
            } else {
                tvGame.setTextAppearance(requireContext(), R.style.WhiteEditText_Regular);
            }
            tvGame.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)

            tvGame.text = "- ${game.name}"
            linear_layout_games.addView(tvGame, layoutParams)
        }
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, backgroundColor)
        button_add_game.visibility = if(enable) View.VISIBLE else View.GONE
    }

    private fun addGame() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.games_dialog, null)

        dialogView.recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val orderedGames = Constants.orderGamesBy(allGames, sharedPrefHandler.getSortingKey())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        if (orderedGames.isNotEmpty()) {
            dialogView.recycler_view_games.adapter = GamesAdapter(requireContext(), orderedGames, platforms, states, sagaId ?: 0, this)
        }
        dialogView.recycler_view_games.visibility = if (orderedGames.isNotEmpty()) View.VISIBLE else View.GONE
        dialogView.layout_empty_list.visibility = if (orderedGames.isNotEmpty()) View.GONE else View.VISIBLE

        dialogView.button_accept.setOnClickListener {
            showGames(newGames)
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun deleteSaga() {

        currentSaga?.let {
            showPopupConfirmationDialog(resources.getString(R.string.SAGA_DETAIL_DELETE_CONFIRMATION)) {

                showLoading()
                sagaAPIClient.deleteSaga(it.id, {
                    sagaRepository.deleteSaga(it)

                    hideLoading()
                    activity?.finish()
                }, {
                    manageError(it)
                })
            }
        }
    }

    private fun editSaga(){

        showEditButton(true)
        enableEdition(true)
    }

    private fun saveSaga() {

        val saga = SagaResponse(
            sagaId ?: 0,
            edit_text_name.text.toString(),
            newGames
        )

        showLoading()
        if (currentSaga != null) {

            sagaAPIClient.setSaga(saga, {
                sagaRepository.updateSaga(it)
                removeSagaFromGames(saga)
                updateGames(saga)

                currentSaga = it
                cancelEdition()
                hideLoading()
            }, {
                manageError(it)
            })
        } else {

            sagaAPIClient.createSaga(saga, {
                sagaAPIClient.getSagas({ sagas ->

                    for (saga in sagas) {
                        sagaRepository.insertSaga(saga)
                    }
                    sagas.firstOrNull { it.games.firstOrNull { it.id == newGames.firstOrNull()?.id } != null }?.let {
                        updateGames(it)
                    }
                    hideLoading()
                    activity?.finish()
                }, {
                    manageError(it)
                })
            }, {
                manageError(it)
            })
        }
    }

    private fun removeSagaFromGames(saga: SagaResponse) {

        val games = gameRepository.getGames().filter { it.saga?.id  == saga.id }
        for (game in games) {
            if (newGames.firstOrNull { it.id == game.id } == null) {

                game.saga = null
                gameRepository.updateGame(game)
            }
        }

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newGame in newGames) {
            newGame.saga = sagaVar
            gameRepository.updateGame(newGame)
        }
    }

    private fun updateGames(saga: SagaResponse) {

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newGame in newGames) {
            newGame.saga = sagaVar
            gameRepository.updateGame(newGame)
        }
    }

    private fun cancelEdition(){

        showEditButton(false)
        showData(currentSaga)
        enableEdition(false)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }
}
