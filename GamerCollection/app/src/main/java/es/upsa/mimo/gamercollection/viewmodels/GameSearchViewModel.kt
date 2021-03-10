package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class GameSearchViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorResponse>()
    private val _games = MutableLiveData<List<GameResponse>>()

    //MARK: - Public properties

    val swipeRefresh: Boolean
        get() = sharedPreferencesHandler.getSwipeRefresh()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val states: List<StateResponse>
        get() = stateRepository.getStatesDatabase()
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorResponse> = _gamesError
    val games: LiveData<List<GameResponse>> = _games

    //MARK: - Public methods

    fun loadGames() {
        //TODO: load games from RAWG
    }
}