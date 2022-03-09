package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.fragments.GamesFragment.ScrollPosition
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class GameSearchViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository
) : ViewModel() {

    //region Private properties
    private var page: Int = 1
    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorResponse>()
    private val _games = MutableLiveData<MutableList<GameResponse>>()
    private val _gamesCount = MutableLiveData<Int>()
    private val _scrollPosition = MutableLiveData(ScrollPosition.TOP)
    //endregion

    //region Public properties
    var query: String? = null
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.swipeRefresh
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorResponse> = _gamesError
    val games: LiveData<MutableList<GameResponse>> = _games
    val gamesCount: LiveData<Int> = _gamesCount
    val scrollPosition: LiveData<ScrollPosition> = _scrollPosition
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
    private fun addGames(newGames: List<GameResponse>, next: Boolean) {

        val currentGames = _games.value ?: mutableListOf()
        if (currentGames.size > 0) {
            currentGames.removeLast()
        }
        currentGames.addAll(newGames)
        if (next) {
            currentGames.add(
                GameResponse(
                    0,
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
                    listOf()
                )
            )
        }
        _games.value = currentGames
    }
    //endregion
}