package es.upsa.mimo.gamercollection.presentation.gamesearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.utils.ScrollPosition
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import javax.inject.Inject

@HiltViewModel
class GameSearchViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    //region Private properties
    private var page: Int = 1
    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorModel>()
    private val _games = MutableLiveData<MutableList<Game>>()
    private val _gamesCount = MutableLiveData(0)
    private val _scrollPosition = MutableLiveData(ScrollPosition.TOP)
    //endregion

    //region Public properties
    var query: String? = null
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.swipeRefresh
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorModel> = _gamesError
    val games: LiveData<MutableList<Game>> = _games
    val gamesCount: LiveData<Int> = _gamesCount
    val scrollPosition: LiveData<ScrollPosition> = _scrollPosition
    //endregion

    //region Lifecycle methods
    init {
        loadGames()
    }
    //endregion

    //region Public methods
    fun loadGames() {

        _gamesLoading.value = true
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

    fun resetPage() {

        page = 1
        _games.value = mutableListOf()
    }
    //endregion

    //region Private methods
    private fun addGames(newGames: List<Game>, next: Boolean) {

        val currentGames = _games.value ?: mutableListOf()
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
                    mutableListOf()
                )
            )
        }
        _games.value = currentGames
    }
    //endregion
}