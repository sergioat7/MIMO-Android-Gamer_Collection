package es.upsa.mimo.gamercollection.presentation.sagadetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SagaDetailViewModel @Inject constructor(
    state: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository,
) : ViewModel() {

    //region Private properties
    private var sagaId: Int = state["sagaId"] ?: 0
    private val _sagaDetailLoading = MutableStateFlow<Boolean?>(null)
    private val _sagaDetailSuccessMessage = MutableStateFlow<Int?>(null)
    private val _sagaDetailError = MutableStateFlow<ErrorModel?>(null)
    private val _saga = MutableStateFlow<Saga?>(null)
    //endregion

    //region Public properties
    lateinit var games: List<Game>
    val sagaDetailLoading: StateFlow<Boolean?> = _sagaDetailLoading
    val sagaDetailSuccessMessage: StateFlow<Int?> = _sagaDetailSuccessMessage
    val sagaDetailError: StateFlow<ErrorModel?> = _sagaDetailError
    val saga: StateFlow<Saga?> = _saga
    //endregion

    //region Lifecycle methods
    init {
        viewModelScope.launch {

            if (sagaId >= 0) {

                _sagaDetailLoading.value = true
                _saga.value = sagaRepository.getSagaDatabase(sagaId)
                _sagaDetailLoading.value = false
            } else {
                _saga.value = null
            }

            games = gameRepository.getGamesDatabase()
        }
    }
    //endregion

    //region Public methods
    fun getOrderedGames(games: List<Game>): List<Game> = when (SharedPreferencesHelper.sortParam) {
        "platform" -> games.sortedBy { it.platform }
        "releaseDate" -> games.sortedBy { it.releaseDate }
        "purchaseDate" -> games.sortedBy { it.purchaseDate }
        "price" -> games.sortedBy { it.price }
        "score" -> games.sortedBy { it.score }
        else -> games.sortedBy { it.name }
    }

    fun saveSaga(name: String, games: List<Game>) {
        val newSaga = Saga(sagaId, name, games)
        if (_saga.value != null) {
            setSaga(newSaga)
        } else {
            createSaga(newSaga)
        }
    }

    fun deleteSaga() {
        viewModelScope.launch {
            _saga.value?.let { saga ->

                _sagaDetailLoading.value = true
                sagaRepository.deleteSaga(saga)
                _sagaDetailLoading.value = false
                _sagaDetailSuccessMessage.value = R.string.saga_removed
            } ?: run {
                _sagaDetailError.value = null
            }
        }
    }
    //endregion

    //region Private methods
    private fun createSaga(saga: Saga) {
        viewModelScope.launch {
            _sagaDetailLoading.value = true
            val newSagaCreated = sagaRepository.createSaga(saga)
            gameRepository.updateSagaGames(newSagaCreated)
            _sagaDetailLoading.value = false
            _sagaDetailSuccessMessage.value = R.string.saga_created
        }
    }

    private fun setSaga(saga: Saga) {
        viewModelScope.launch {
            _sagaDetailLoading.value = true
            sagaRepository.setSaga(saga)
            gameRepository.removeSagaFromGames(saga)
            gameRepository.updateSagaGames(saga)
            _saga.value = saga
            _sagaDetailLoading.value = false
        }
    }
    //endregion
}