package es.upsa.mimo.gamercollection.presentation.gamesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.utils.ScrollPosition
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GameSearchViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    //region Private properties
    private var page: Int = 1
    private val _gamesLoading = MutableStateFlow<Boolean?>(null)
    private val _gamesError = MutableStateFlow<ErrorModel?>(null)
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    private val _gamesCount = MutableStateFlow(0)
    private val _scrollPosition = MutableStateFlow(ScrollPosition.TOP)
    //endregion

    //region Public properties
    var query: String? = null
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.swipeRefresh
    val gamesLoading: StateFlow<Boolean?> = _gamesLoading
    val gamesError: StateFlow<ErrorModel?> = _gamesError
    val games: StateFlow<List<Game>> = _games
    val gamesCount: StateFlow<Int> = _gamesCount
    val scrollPosition: StateFlow<ScrollPosition> = _scrollPosition
    //endregion

    //region Lifecycle methods
    init {
        loadGames()
    }
    //endregion

    //region Public methods
    fun loadGames() {
        viewModelScope.launch {
            _gamesLoading.value = true
            _gamesError.value = null
            gameRepository.getRawgGames(page, query, { newGames, gamesCount, next ->

                _gamesLoading.value = false
                addGames(newGames, next)
                if (page == 1) {
                    _scrollPosition.value = ScrollPosition.TOP
                    _gamesCount.value = gamesCount
                }
                page += 1
            }, {
                _gamesError.value = it
            })
        }
    }

    fun resetPage() {
        page = 1
        _games.value = emptyList()
    }
    //endregion

    //region Private methods
    private fun addGames(newGames: List<Game>, next: Boolean) {
        val currentGames = _games.value.toMutableList()
        if (currentGames.isNotEmpty()) {
            currentGames.removeAt(currentGames.lastIndex)
        }
        currentGames.addAll(newGames)
        if (next) {
            currentGames.add(
                Game(
                    -1,
                    null,
                    null,
                    0.0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    0.0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    mutableListOf(),
                ),
            )
        }
        _games.value = currentGames
    }
    //endregion
}