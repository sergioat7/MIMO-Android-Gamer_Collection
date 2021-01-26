package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import javax.inject.Inject

class GameDetailViewModel @Inject constructor(
    private val gameAPIClient: GameAPIClient,
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository
): ViewModel() {

    //MARK: - Private properties

    private var gameId: Int? = null
    private val _gameDetailLoading = MutableLiveData<Boolean>()
    private val _gameDetailError = MutableLiveData<ErrorResponse>()
    private val _game = MutableLiveData<GameResponse?>()

    //MARK: - Public properties

    val platforms: List<PlatformResponse> = platformRepository.getPlatforms()
    val gameDetailLoading: LiveData<Boolean> = _gameDetailLoading
    val gameDetailError: LiveData<ErrorResponse> = _gameDetailError
    val game: LiveData<GameResponse?> = _game

    //MARK: - Public methods

    fun getGame() {

        gameId?.let {

            _gameDetailLoading.value = true
            _game.value = gameRepository.getGame(it)
            _gameDetailLoading.value = false
        } ?: run {
            _game.value = null
        }
    }

    fun saveGame(name: String,
                 platform: String?,
                 score: Double,
                 imageUrl: String?,
                 game: GameResponse?) {

        if (name.isEmpty() && platform == null && score == 0.0 && imageUrl == null && game == null) return

        val newGame = game?.let {
            it.name = name
            it.platform = platform
            it.imageUrl = imageUrl
            it.score = score
            it
        } ?: run {
            GameResponse(
                _game.value?.id ?: 0,
                name,
                platform,
                score,
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
                imageUrl,
                null,
                null,
                null,
                null,
                ArrayList())
        }

        _gameDetailLoading.value = true
        if (_game.value != null) {

            gameAPIClient.setGame(newGame, {
                gameRepository.updateGame(it)

                _game.value = it
                _gameDetailLoading.value = false
            }, {
                _gameDetailError.value = it
            })
        } else {

            gameAPIClient.createGame(newGame, {
                gameAPIClient.getGames({ games ->

                    for (g in games) {
                        gameRepository.insertGame(g)
                    }
                    _gameDetailLoading.value = false
                    _gameDetailError.value = null
                }, {
                    _gameDetailError.value = it
                })
            }, {
                _gameDetailError.value = it
            })
        }
    }

    fun setGameId(gameId: Int?) {
        this.gameId = gameId
    }
}