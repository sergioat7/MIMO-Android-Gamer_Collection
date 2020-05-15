package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.fragment_games.*

class GamesFragment : BaseFragment(), GamesAdapter.OnItemClickListener {

    private lateinit var gameRepository: GameRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameRepository = GameRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.games_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_synchronize -> {
                openSyncPopup()
                return true
            }
            R.id.action_filter -> {
                filter()
                return true
            }
            R.id.action_add -> {
                add()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(gameId: Int) {
        
        val params = mapOf("gameId" to gameId)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val games = gameRepository.getGames()

        setGamesCount(games)
        button_sort.setOnClickListener { sort() }

        button_pending.setOnClickListener {
            it.isSelected = !it.isSelected
            button_in_progress.isSelected = false
            button_finished.isSelected = false
            showPendingGames()
        }
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            it.isSelected = !it.isSelected
            button_finished.isSelected = false
            showInProgressGames()
        }
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            it.isSelected = !it.isSelected
            showFinishedGames()
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_games.adapter = GamesAdapter(requireContext(), resources, games, platforms, states, this)
    }

    private fun setGamesCount(games: List<GameResponse>) {

        val filteredGames = games.mapNotNull { it.state }
        val pendingGamesCount = filteredGames.filter { it == Constants.pending }.size
        val inProgressGamesCount = filteredGames.filter { it == Constants.inProgress }.size
        val finishedGamesCount = filteredGames.filter { it == Constants.finished }.size

        text_view_games_number.text = resources.getString(R.string.GAMES_NUMBER_TITLE, games.size)
        button_pending.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_PENDING, pendingGamesCount)
        button_in_progress.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_IN_PROGRESS, inProgressGamesCount)
        button_finished.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_FINISHED, finishedGamesCount)
    }

    private fun filter() {
        //TODO
    }

    private fun add() {
        launchActivity(GameDetailActivity::class.java)
    }

    private fun sort() {
        //TODO
    }

    private fun showPendingGames() {
        //TODO
    }

    private fun showInProgressGames() {
        //TODO
    }

    private fun showFinishedGames() {
        //TODO
    }
}
