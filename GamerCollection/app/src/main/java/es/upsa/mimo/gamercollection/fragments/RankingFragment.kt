package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.sqlite.db.SimpleSQLiteQuery
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.SettingsActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.fragments.popups.OnFiltersSelected
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : BaseFragment(), GamesAdapter.OnItemClickListener, OnFiltersSelected {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var gameRepository: GameRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository
    private var currentFilters: FilterModel? = null

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
        getContent(currentFilters)
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
            R.id.action_settings -> {
                settings()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(gameId: Int) {

        val params = mapOf("gameId" to gameId)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    override fun filter(filters: FilterModel) {

        currentFilters = filters
        getContent(currentFilters)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        swipe_refresh_layout.isEnabled = sharedPrefHandler.getSwipeRefresh()
        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
        swipe_refresh_layout.setOnRefreshListener {
            loadGames()
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_games.adapter = GamesAdapter(requireContext(), ArrayList(), platforms, states, null, this)
    }

    private fun getContent(filters: FilterModel?) {

        var queryString = "SELECT * FROM Game"

        var queryConditions = " WHERE "

        filters?.let { filters ->

            var queryPlatforms = ""
            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                queryPlatforms += ""
                for (platform in platforms) {
                    queryPlatforms += "platform == '${platform}' OR "
                }
                queryPlatforms = queryPlatforms.dropLast(4) + " AND "
            }

            var queryGenres = ""
            val genres = filters.genres
            if (genres.isNotEmpty()){
                for (genre in genres) {
                    queryGenres += "genre == '${genre}' OR "
                }
                queryGenres = queryGenres.dropLast(4) + " AND "
            }

            var queryFormats = ""
            val formats = filters.formats
            if (formats.isNotEmpty()){
                for (format in formats) {
                    queryFormats += "format == '${format}' OR "
                }

                queryFormats = queryFormats.dropLast(4) + " AND "
            }

            queryConditions += queryPlatforms + queryGenres + queryFormats

            queryConditions += if (filters.minScore > 0) "score >= " else "score > "
            queryConditions += "${filters.minScore} AND score <= ${filters.maxScore} AND "

            if (filters.minReleaseDate != null) {
                queryConditions += "releaseDate >= '${filters.minReleaseDate}' AND "
            }
            if (filters.maxReleaseDate != null) {
                queryConditions += "releaseDate <= '${filters.maxReleaseDate}' AND "
            }

            if (filters.minPurchaseDate != null) {
                queryConditions += "purchaseDate >= '${filters.minPurchaseDate}' AND "
            }
            if (filters.maxPurchaseDate != null) {
                queryConditions += "purchaseDate <= '${filters.maxPurchaseDate}' AND "
            }

            queryConditions += "price >= ${filters.minPrice} AND "
            if (filters.maxPrice > 0) {
                queryConditions += "price <= ${filters.maxPrice} AND "
            }

            if (filters.isGoty) {
                queryConditions += "goty == 1 AND "
            }

            if (filters.isLoaned) {
                queryConditions += "loanedTo != null AND "
            }

            if (filters.hasSaga) {
                queryConditions += "saga_id != -1 AND "
            }

            if (filters.hasSongs) {
                queryConditions += "songs != '[]' AND "
            }
        } ?: run {
            queryConditions += "score > 0 AND "
        }
        queryConditions = queryConditions.dropLast(5)
        queryString += queryConditions

        queryString += " ORDER BY score DESC, name ASC"

        val query = SimpleSQLiteQuery(queryString)
        val games = gameRepository.getGames(query)

        val adapter = recycler_view_games.adapter
        if (adapter != null && adapter is GamesAdapter) {
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
            getContent(currentFilters)
            swipe_refresh_layout.isRefreshing = false
        }, {
            manageError(it)
        })
    }

    private fun filter() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("filterPopup")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupFilterDialogFragment(currentFilters, this)
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, "filterPopup")
    }

    private fun settings() {
        launchActivity(SettingsActivity::class.java)
    }
}
