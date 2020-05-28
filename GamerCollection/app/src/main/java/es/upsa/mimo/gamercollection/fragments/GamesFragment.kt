package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.NumberPicker
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
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_games.*
import kotlinx.android.synthetic.main.state_button.view.*

class GamesFragment : BaseFragment(), GamesAdapter.OnItemClickListener, OnFiltersSelected {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var gameRepository: GameRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository
    private var menu: Menu? = null
    private val sortingKeys = arrayOf("name", "platform", "releaseDate", "purchaseDate", "price")
    private var sortingValues = arrayOf("")
    private var state: String? = null
    private lateinit var sortKey: String
    private var sortAscending = true
    private var currentFilters: FilterModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        gameRepository = GameRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())
        sortingValues = arrayOf(resources.getString(R.string.SORT_NAME), resources.getString(R.string.SORT_PLATFORM), resources.getString(R.string.SORT_RELEASE_DATE), resources.getString(R.string.SORT_PURCHASE_DATE), resources.getString(R.string.SORT_PRICE))
        sortKey = sharedPrefHandler.getSortingKey()

        initializeUI()
    }

    override fun onResume() {
        super.onResume()

        val games = getContent(state, sortKey, sortAscending, currentFilters)
        setGamesCount(games)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
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
            R.id.action_filter_on -> {
                filter()
                return true
            }
            R.id.action_add -> {
                add()
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

    override fun filter(filters: FilterModel?) {

        currentFilters = filters
        val games = getContent(state, sortKey, sortAscending, currentFilters)
        setGamesCount(games)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        button_sort.setOnClickListener { sort() }

        button_pending.setOnClickListener {
            it.isSelected = !it.isSelected
            button_in_progress.isSelected = false
            button_finished.isSelected = false
            swipe_refresh_layout.isEnabled = !it.isSelected && sharedPrefHandler.getSwipeRefresh()
            state = if (it.isSelected) Constants.pending else null
            getContent(state, sortKey, sortAscending, currentFilters)
        }
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            it.isSelected = !it.isSelected
            button_finished.isSelected = false
            swipe_refresh_layout.isEnabled = !it.isSelected && sharedPrefHandler.getSwipeRefresh()
            state = if (it.isSelected) Constants.inProgress else null
            getContent(state, sortKey, sortAscending, currentFilters)
        }
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            it.isSelected = !it.isSelected
            swipe_refresh_layout.isEnabled = !it.isSelected && sharedPrefHandler.getSwipeRefresh()
            state = if (it.isSelected) Constants.finished else null
            getContent(state, sortKey, sortAscending, currentFilters)
        }

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

    private fun getContent(state: String?, sortKey: String?, sortAscending: Boolean, filters: FilterModel?): List<GameResponse> {

        var queryString = "SELECT * FROM Game"

        var queryConditions = when(state) {
            Constants.pending -> " WHERE state == '${Constants.pending}' AND "
            Constants.inProgress -> " WHERE state == '${Constants.inProgress}' AND "
            Constants.finished -> " WHERE state == '${Constants.finished}' AND "
            else -> ""
        }

        menu?.let{
            it.findItem(R.id.action_filter).isVisible = filters == null
            it.findItem(R.id.action_filter_on).isVisible = filters != null
        }

        filters?.let { filters ->

            if (queryConditions.isEmpty()) queryConditions += " WHERE "

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

            queryConditions += "score >= ${filters.minScore} AND score <= ${filters.maxScore} AND "

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
        }
        queryConditions = queryConditions.dropLast(5)
        queryString += queryConditions

        val sortParam = sortKey ?: sharedPrefHandler.getSortingKey()
        val sortOrder = if(sortAscending) "ASC"  else "DESC"
        queryString += " ORDER BY $sortParam $sortOrder"

        val query = SimpleSQLiteQuery(queryString)
        val games = gameRepository.getGames(query)

        val adapter = recycler_view_games.adapter
        if (adapter != null && adapter is GamesAdapter) {
            adapter.games = games
            adapter.notifyDataSetChanged()
        }
        layout_empty_list.visibility = if (games.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (games.isNotEmpty()) View.VISIBLE else View.GONE

        return games
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

    private fun add() {
        launchActivity(GameDetailActivity::class.java)
    }

    private fun settings() {
        launchActivity(SettingsActivity::class.java)
    }

    private fun sort() {

        val dialogView = LinearLayout(requireContext())
        dialogView.orientation = LinearLayout.HORIZONTAL

        val ascendingPicker = getPicker(arrayOf(resources.getString(R.string.ASCENDING), resources.getString(R.string.DESCENDING)))
        val ascendingPickerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ascendingPickerParams.weight = 1f

        val sortKeysPicker = getPicker(sortingValues)
        val sortKeysPickerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sortKeysPickerParams.weight = 1f

        val params = LinearLayout.LayoutParams(50, 50)
        params.gravity = Gravity.CENTER

        dialogView.layoutParams = params
        dialogView.addView(sortKeysPicker, sortKeysPickerParams)
        dialogView.addView(ascendingPicker, ascendingPickerParams)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.SORT_TITLE))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ACCEPT)) { dialog, _ ->
                sortKey = sortingKeys[sortKeysPicker.value]
                sortAscending = ascendingPicker.value == 0
                getContent(state, sortKey, sortAscending, currentFilters)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.CANCEL)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadGames() {

        enableStateButtons(false)
        gameAPIClient.getGames({

            for (game in it) {
                gameRepository.insertGame(game)
            }
            gameRepository.removeDisableContent(it)
            val games = getContent(null, sortKey, sortAscending, currentFilters)
            setGamesCount(games)
            enableStateButtons(true)
        }, {

            manageError(it)
            enableStateButtons(true)
        })
    }

    private fun enableStateButtons(enable: Boolean) {

        swipe_refresh_layout.isRefreshing = !enable
        button_pending.isEnabled = enable
        button_in_progress.isEnabled = enable
        button_finished.isEnabled = enable
    }

    private fun getPicker(values: Array<String>): NumberPicker {

        val picker = NumberPicker(requireContext())
        picker.minValue = 0
        picker.maxValue = values.size - 1
        picker.wrapSelectorWheel = true
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        picker.displayedValues = values
        return picker
    }
}
