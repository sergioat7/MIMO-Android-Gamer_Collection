package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.sqlite.db.SimpleSQLiteQuery
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : BaseFragment(), GamesAdapter.OnItemClickListener {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var gameRepository: GameRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        gameRepository = GameRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())

        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        getContent()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.ranking_toolbar_menu, menu)
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(gameId: Int) {

        val params = mapOf("gameId" to gameId)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
        swipe_refresh_layout.setOnRefreshListener {
            loadGames()
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_games.adapter = GamesAdapter(requireContext(), ArrayList(), platforms, states, this)
    }

    private fun getContent() {

        var queryString = "SELECT * FROM Game WHERE score > 0"

        queryString += " ORDER BY score DESC, name ASC"

        val query = SimpleSQLiteQuery(queryString)
        val games = gameRepository.getGames(query)

        val adapter = recycler_view_games.adapter
        if (adapter is GamesAdapter) {
            adapter.games = games
            adapter.notifyDataSetChanged()
        }
        layout_empty_list.visibility = if (games.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (games.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadGames() {

        gameAPIClient.getGames({

            for (game in it) {
                gameRepository.insertGame(game)
            }
            gameRepository.removeDisableContent(it)
            getContent()
            swipe_refresh_layout.isRefreshing = false
        }, {
            manageError(it)
        })
    }

    private fun filter(){
        //TODO
    }
}
