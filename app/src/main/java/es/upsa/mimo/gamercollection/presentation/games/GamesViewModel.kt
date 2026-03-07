package es.upsa.mimo.gamercollection.presentation.games

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.FilterModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.extensions.getPickerParams
import es.upsa.mimo.gamercollection.extensions.setup
import es.upsa.mimo.gamercollection.utils.ScrollPosition
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _gamesLoading = MutableStateFlow<Boolean?>(null)
    private val _gamesError = MutableStateFlow<ErrorModel?>(null)
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    private val _gamesCount = MutableStateFlow<List<Game>>(emptyList())
    private val _gameDeleted = MutableStateFlow<Int?>(null)
    private var _state = MutableStateFlow<String?>(null)
    private var _filters = MutableStateFlow<FilterModel?>(null)
    private var _scrollPosition = MutableStateFlow(ScrollPosition.TOP)
    private val originalGames = MutableStateFlow<List<Game>>(emptyList())
    private var sortParam: String = userRepository.sortParam
    private var isSortOrderAscending = userRepository.isSortOrderAscending
    private var query: String? = null
    //endregion

    //region Public properties
    val language: String
        get() = userRepository.language
    val dateFormatToShow: String
        get() = userRepository.dateFormatToShow
    val filterDateFormat: String
        get() = userRepository.filterDateFormat
    val swipeRefresh: Boolean
        get() = userRepository.swipeRefresh
    val gamesLoading: StateFlow<Boolean?> = _gamesLoading
    val gamesError: StateFlow<ErrorModel?> = _gamesError
    val games: StateFlow<List<Game>> = _games
    val gamesCount: StateFlow<List<Game>> = _gamesCount
    val gameDeleted: StateFlow<Int?> = _gameDeleted
    val state: StateFlow<String?> = _state
    val filters: StateFlow<FilterModel?> = _filters
    val scrollPosition: StateFlow<ScrollPosition> = _scrollPosition
    //endregion

    //region Public methods
    fun loadGames() {
        _gamesLoading.value = true
        resetProperties()
        fetchGames()
        _gamesLoading.value = false
    }

    fun fetchGames() {
        viewModelScope.launch {
            val games = gameRepository.getGamesDatabase(
                _filters.value,
                query,
                sortParam,
                isSortOrderAscending,
            )
            originalGames.value = games

            if (!_state.value.isNullOrBlank()) {
                _games.value = games.filter { it.state == _state.value }
            } else {
                _games.value = games
            }

            _gamesCount.value = games

            _scrollPosition.value = ScrollPosition.TOP
        }
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
            }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun isNotificationLaunched(gameId: Int): Boolean = userRepository.isNotificationLaunched(gameId)

    fun setNotificationLaunched(gameId: Int, value: Boolean) {
        userRepository.setNotificationLaunched(gameId, value)
    }

    fun deleteGame(position: Int) {
        viewModelScope.launch {
            if (_games.value.isEmpty()) return@launch

            _gamesLoading.value = true
            val game = _games.value[position]
            gameRepository.deleteGame(game)
            _games.value.firstOrNull { it.id == game.id }?.let {
                _games.value = _games.value.minus(it)
            }
            _gameDeleted.value = position
            _gameDeleted.value = null
            _gamesLoading.value = false
        }
    }

    fun searchGames(query: String) {
        this.query = query
        fetchGames()
    }

    fun setState(newState: String?) {
        _state.value = newState
        if (!newState.isNullOrBlank()) {
            _games.value = originalGames.value.filter { it.state == newState }
        } else {
            _games.value = originalGames.value
        }
    }

    fun applyFilters(newFilters: FilterModel?) {
        _filters.value = newFilters
        fetchGames()
    }

    fun setPosition(newPosition: ScrollPosition) {
        _scrollPosition.value = newPosition
    }
    //endregion

    //region Private methods
    private fun resetProperties() {
        _state.value = null
        _filters.value = null
        sortParam = userRepository.sortParam
        isSortOrderAscending = true
        query = null
    }
    //endregion
}