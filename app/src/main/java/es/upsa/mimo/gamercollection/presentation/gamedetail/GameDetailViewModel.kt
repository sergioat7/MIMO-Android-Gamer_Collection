package es.upsa.mimo.gamercollection.presentation.gamedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    state: SavedStateHandle,
    private val gameRepository: GameRepository,
) : ViewModel() {

    //region Private properties
    private var gameId: Int = state["gameId"] ?: -1
    private var isRawgGame: Boolean = state["isRawgGame"] ?: false
    private val _gameDetailLoading = MutableStateFlow<Boolean?>(null)
    private val _gameDetailSuccessMessage = MutableStateFlow<Int?>(null)
    private val _gameDetailError = MutableStateFlow<ErrorModel?>(null)
    private val _game = MutableLiveData<Game?>()
    //endregion

    //region Public properties
    val gameDetailLoading: StateFlow<Boolean?> = _gameDetailLoading
    val gameDetailSuccessMessage: StateFlow<Int?> = _gameDetailSuccessMessage
    val gameDetailError: StateFlow<ErrorModel?> = _gameDetailError
    val game: LiveData<Game?> = _game
    //endregion

    //region Lifecycle methods
    init {
        runBlocking {

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
    }
    //endregion

    //region Public methods
    fun createGame(game: Game) {
        viewModelScope.launch {
            _gameDetailLoading.value = true
            gameRepository.createGame(game)
            _gameDetailLoading.value = false
            _gameDetailSuccessMessage.value = R.string.game_created
        }
    }

    fun setGame(game: Game) {
        viewModelScope.launch {
            _gameDetailLoading.value = true
            _game.value = gameRepository.setGame(game)
            _gameDetailLoading.value = false
        }
    }

    fun deleteGame() {
        viewModelScope.launch {
            _game.value?.let { game ->

                _gameDetailLoading.value = true
                gameRepository.deleteGame(game)
                _gameDetailLoading.value = false
                _gameDetailSuccessMessage.value = R.string.game_removed
            }
        }
    }
    //endregion
}