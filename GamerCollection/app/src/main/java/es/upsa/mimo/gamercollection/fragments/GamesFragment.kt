package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.sqlite.db.SimpleSQLiteQuery
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
import kotlinx.android.synthetic.main.state_button.view.*

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

        button_sort.setOnClickListener { sort() }

        button_pending.setOnClickListener {
            it.isSelected = !it.isSelected
            button_in_progress.isSelected = false
            button_finished.isSelected = false
            getContent(if (it.isSelected) Constants.pending else null)
        }
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            it.isSelected = !it.isSelected
            button_finished.isSelected = false
            getContent(if (it.isSelected) Constants.inProgress else null)
        }
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            it.isSelected = !it.isSelected
            getContent(if (it.isSelected) Constants.finished else null)
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val games = gameRepository.getGames()
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_games.adapter = GamesAdapter(requireContext(), games, platforms, states, this)

        layout_empty_list.visibility = if (games.isNotEmpty()) View.GONE else View.VISIBLE
        recycler_view_games.visibility = if (games.isNotEmpty()) View.VISIBLE else View.GONE

        setGamesCount(games)
    }

    private fun getContent(state: String?) {

        var queryString = "SELECT * FROM Game"
        queryString += when(state) {
            Constants.pending -> " WHERE state == '${Constants.pending}'"
            Constants.inProgress -> " WHERE state == '${Constants.inProgress}'"
            Constants.finished -> " WHERE state == '${Constants.finished}'"
            else -> ""
        }

        val query = SimpleSQLiteQuery(queryString)
        val games = gameRepository.getGames(query)

        if (games.isNotEmpty()) {

            val adapter = recycler_view_games.adapter as? GamesAdapter
            if (adapter != null) {
                adapter.games = games
                adapter.notifyDataSetChanged()
            }
        }
        layout_empty_list.visibility = if (games.isNotEmpty()) View.GONE else View.VISIBLE
        recycler_view_games.visibility = if (games.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setGamesCount(games: List<GameResponse>) {

        val filteredGames = games.mapNotNull { it.state }
        val pendingGamesCount = filteredGames.filter { it == Constants.pending }.size
        val inProgressGamesCount = filteredGames.filter { it == Constants.inProgress }.size
        val finishedGamesCount = filteredGames.filter { it == Constants.finished }.size

        text_view_games_number.text = resources.getString(R.string.GAMES_NUMBER_TITLE, games.size)
        button_pending.text_view_subtitle.text = "$pendingGamesCount"
        button_in_progress.text_view_subtitle.text = "$inProgressGamesCount"
        button_finished.text_view_subtitle.text = "$finishedGamesCount"
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
}
