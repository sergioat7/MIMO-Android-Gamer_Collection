package es.upsa.mimo.gamercollection.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class GamesViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    stateRepository: StateRepository,
    private val gameAPIClient: GameAPIClient
): ViewModel() {

    //MARK: - Private properties

    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorResponse>()
    private val _games = MutableLiveData<List<GameResponse>>()
    private val _gamesCount = MutableLiveData<List<GameResponse>>()

    //MARK: - Public properties

    val language: String = sharedPreferencesHandler.getLanguage()
    val swipeRefresh: Boolean = sharedPreferencesHandler.getSwipeRefresh()
    val platforms: List<PlatformResponse> = platformRepository.getPlatforms()
    val states: List<StateResponse> = stateRepository.getStates()
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorResponse> = _gamesError
    val games: LiveData<List<GameResponse>> = _games
    val gamesCount: LiveData<List<GameResponse>> = _gamesCount
    var state: String? = null
    var sortKey: String = sharedPreferencesHandler.getSortingKey()
    var sortAscending = true
    var filters: FilterModel? = null

    //MARK: - Public methods

    fun loadGames() {

        _gamesLoading.value = true
        gameAPIClient.getGames({

            gameRepository.manageGames(it)
            resetProperties()
            getGames()
            _gamesLoading.value = false
        }, {

            _gamesLoading.value = true
            _gamesError.value = it
        })
    }

    fun getGames() {

        var queryString = "SELECT * FROM Game"

        var queryConditions = when(state) {
            Constants.PENDING_STATE -> " WHERE state == '${Constants.PENDING_STATE}' AND "
            Constants.IN_PROGRESS_STATE -> " WHERE state == '${Constants.IN_PROGRESS_STATE}' AND "
            Constants.FINISHED_STATE -> " WHERE state == '${Constants.FINISHED_STATE}' AND "
            else -> ""
        }

        filters?.let { filters ->

            if (queryConditions.isEmpty()) queryConditions += " WHERE "

            var queryPlatforms = ""
            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                queryPlatforms += "("
                for (platform in platforms) {
                    queryPlatforms += "platform == '${platform}' OR "
                }
                queryPlatforms = queryPlatforms.dropLast(4) + ") AND "
            }

            var queryGenres = ""
            val genres = filters.genres
            if (genres.isNotEmpty()){
                queryGenres += "("
                for (genre in genres) {
                    queryGenres += "genre == '${genre}' OR "
                }
                queryGenres = queryGenres.dropLast(4) + ") AND "
            }

            var queryFormats = ""
            val formats = filters.formats
            if (formats.isNotEmpty()){
                queryFormats += "("
                for (format in formats) {
                    queryFormats += "format == '${format}' OR "
                }

                queryFormats = queryFormats.dropLast(4) + ") AND "
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

        val sortOrder = if(sortAscending) "ASC"  else "DESC"
        queryString += " ORDER BY $sortKey $sortOrder, name ASC"

        val query = SimpleSQLiteQuery(queryString)
        val games = gameRepository.getGames(query)

        _games.value = games

        if (_gamesCount.value == null) {
            _gamesCount.value = games
        }
    }

    fun sortGames(context: Context, resources: Resources) {

        val sortingKeys = resources.getStringArray(R.array.sorting_keys_ids)
        val sortingValues = resources.getStringArray(R.array.sorting_keys)

        val dialogView = LinearLayout(context)
        dialogView.orientation = LinearLayout.HORIZONTAL

        val ascendingPicker = getPicker(arrayOf(resources.getString(R.string.ascending), resources.getString(R.string.descending)), context)
        val ascendingPickerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ascendingPickerParams.weight = 1f

        val sortKeysPicker = getPicker(sortingValues, context)
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
            .setTitle(resources.getString(R.string.sort_title))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                sortKey = sortingKeys[sortKeysPicker.value]
                sortAscending = ascendingPicker.value == 0
                getGames()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun isNotificationLaunched(gameId: Int): Boolean {
        return sharedPreferencesHandler.notificationLaunched(gameId)
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {
        sharedPreferencesHandler.setNotificationLaunched(gameId, value)
    }

    // MARK: Private methods

    private fun getPicker(values: Array<String>, context: Context): NumberPicker {

        val picker = NumberPicker(context)
        picker.minValue = 0
        picker.maxValue = values.size - 1
        picker.wrapSelectorWheel = true
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        picker.displayedValues = values
        return picker
    }

    private fun resetProperties() {

        state = null
        sortKey = sharedPreferencesHandler.getSortingKey()
        sortAscending = true
        filters = null
    }
}