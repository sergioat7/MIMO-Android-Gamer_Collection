package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import javax.inject.Inject

class GameDetailViewModel @Inject constructor(
    state: SavedStateHandle,
    private val gameRepository: GameRepository
) : ViewModel() {

    //region Private properties
    private var gameId: Int = state["gameId"] ?: -1
    private var isRawgGame: Boolean = state["isRawgGame"] ?: false
    private val _gameDetailLoading = MutableLiveData<Boolean>()
    private val _gameDetailSuccessMessage = MutableLiveData<Int>()
    private val _gameDetailError = MutableLiveData<ErrorResponse>()
    private val _game = MutableLiveData<GameResponse?>()
    //endregion

    //region Public properties
    val gameDetailLoading: LiveData<Boolean> = _gameDetailLoading
    val gameDetailSuccessMessage: LiveData<Int> = _gameDetailSuccessMessage
    val gameDetailError: LiveData<ErrorResponse> = _gameDetailError
    val game: LiveData<GameResponse?> = _game
    //endregion

    //region Lifecycle methods
    init {

        if (gameId >= 0) {
            _gameDetailLoading.value = true
            if (isRawgGame) {

                gameRepository.getRawgGame(gameId, { game ->

                    _game.value = game
                    _gameDetailLoading.value = false
                }, {
                    _gameDetailError.value = it
                })
            } else {

                _game.value = gameRepository.getGameDatabase(gameId)
                _gameDetailLoading.value = false
            }
        } else {
            _game.value = null
        }
    }
    //endregion

    //region Public methods
    fun createGame(game: GameResponse) {

        _gameDetailLoading.value = true
        gameRepository.createGame(game, {

            _gameDetailLoading.value = false
            _gameDetailSuccessMessage.value = R.string.game_created
        }, {
            _gameDetailError.value = it
        })
    }

    fun setGame(game: GameResponse) {

        _gameDetailLoading.value = true
        gameRepository.setGame(game, {

            _game.value = it
            _gameDetailLoading.value = false
        }, {
            _gameDetailError.value = it
        })
    }

    fun deleteGame() {
        _game.value?.let { game ->

            _gameDetailLoading.value = true
            gameRepository.deleteGame(game, {

                _gameDetailLoading.value = false
                _gameDetailSuccessMessage.value = R.string.game_removed
            }, {
                _gameDetailError.value = it
            })
        }
    }
    //endregion
}