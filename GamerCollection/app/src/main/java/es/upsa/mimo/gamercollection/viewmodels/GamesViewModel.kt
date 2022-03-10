package es.upsa.mimo.gamercollection.viewmodels

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.getPickerParams
import es.upsa.mimo.gamercollection.extensions.setup
import es.upsa.mimo.gamercollection.fragments.GamesFragment
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.responses.*
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class GamesViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository
) : ViewModel() {

    //region Private properties
    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorResponse>()
    private val _originalGames = MutableLiveData<List<GameResponse>>()
    private val _games = MutableLiveData<List<GameResponse>>()
    private val _gamesCount = MutableLiveData<List<GameResponse>>()
    private val _gameDeleted = MutableLiveData<Int?>()
    private var _state = MutableLiveData<String?>(null)
    private var _filters = MutableLiveData<FilterModel?>(null)
    private var _scrollPosition = MutableLiveData(GamesFragment.ScrollPosition.TOP)
    private var sortParam: String = SharedPreferencesHelper.sortParam
    private var isSortOrderAscending = true
    private var query: String? = null
    //endregion

    //region Public properties
    val language: String
        get() = SharedPreferencesHelper.language
    val dateFormatToShow: String
        get() = SharedPreferencesHelper.dateFormatToShow
    val filterDateFormat: String
        get() = SharedPreferencesHelper.filterDateFormat
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.swipeRefresh
    val formats: List<FormatResponse>
        get() = formatRepository.getFormatsDatabase()
    val genres: List<GenreResponse>
        get() = genreRepository.getGenresDatabase()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorResponse> = _gamesError
    val games: LiveData<List<GameResponse>> = _games
    val gamesCount: LiveData<List<GameResponse>> = _gamesCount
    val gameDeleted: LiveData<Int?> = _gameDeleted
    val state: LiveData<String?> = _state
    val filters: LiveData<FilterModel?> = _filters
    val scrollPosition: LiveData<GamesFragment.ScrollPosition> = _scrollPosition
    //endregion

    //region Public methods
    fun loadGames() {

        _gamesLoading.value = true
        gameRepository.loadGames({

            resetProperties()
            fetchGames()
            _gamesLoading.value = false
        }, {

            _gamesLoading.value = false
            _gamesError.value = it
        })
    }

    fun fetchGames() {

        val games = gameRepository.getGamesDatabase(
            _filters.value,
            query,
            sortParam,
            isSortOrderAscending
        )
        _originalGames.value = games

        if (!_state.value.isNullOrBlank()) {
            _games.value = _originalGames.value?.filter { game ->
                game.state == _state.value
            } ?: listOf()
        } else {
            _games.value = games
        }

        _gamesCount.value = games

        _scrollPosition.value = GamesFragment.ScrollPosition.TOP
    }

    fun sortGames(context: Context, resources: Resources) {

        val sortingKeys = resources.getStringArray(R.array.sort_param_keys)
        val sortingValues = resources.getStringArray(R.array.sort_param_values)

        val dialogView = LinearLayout(context)
        dialogView.orientation = LinearLayout.HORIZONTAL

        val sortKeysPicker = NumberPicker(context)
        sortKeysPicker.setup(sortingValues)
        sortKeysPicker.value = sortingKeys.indexOf(sortParam)

        val sortOrdersPicker = NumberPicker(context)
        sortOrdersPicker.setup(context.resources.getStringArray(R.array.sort_order_values))
        sortOrdersPicker.value = if (isSortOrderAscending) 0 else 1

        val params = LinearLayout.LayoutParams(50, 50)
        params.gravity = Gravity.CENTER

        dialogView.layoutParams = params
        dialogView.addView(sortKeysPicker, getPickerParams())
        dialogView.addView(sortOrdersPicker, getPickerParams())

        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.sort_title))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                sortParam = sortingKeys[sortKeysPicker.value]
                isSortOrderAscending = sortOrdersPicker.value == 0
                fetchGames()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun isNotificationLaunched(gameId: Int): Boolean {
        return SharedPreferencesHelper.notificationLaunched(gameId)
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {
        SharedPreferencesHelper.setNotificationLaunched(gameId, value)
    }

    fun deleteGame(position: Int) {
        _games.value?.get(position)?.let { game ->

            _gamesLoading.value = true
            gameRepository.deleteGame(game, {

                _games.value?.first { it.id == game.id }?.let { removed ->
                    _games.value = _games.value?.minus(removed)
                }
                _gameDeleted.value = position
                _gameDeleted.value = null
                _gamesLoading.value = false
            }, {

                _gamesLoading.value = false
                _gameDeleted.value = null
            })
        }
    }

    fun searchGames(query: String) {

        this.query = query
        fetchGames()
    }

    fun setState(newState: String?) {

        _state.value = newState
        if (!newState.isNullOrBlank()) {
            _games.value = _originalGames.value?.filter { game ->
                game.state == newState
            } ?: listOf()
        } else {
            _games.value = _originalGames.value
        }
    }

    fun applyFilters(newFilters: FilterModel?) {

        _filters.value = newFilters
        fetchGames()
    }

    fun setPosition(newPosition: GamesFragment.ScrollPosition) {
        _scrollPosition.value = newPosition
    }
    //endregion

    //region Private methods
    private fun resetProperties() {

        _state.value = null
        _filters.value = null
        sortParam = SharedPreferencesHelper.sortParam
        isSortOrderAscending = true
        query = null
    }
    //endregion
}