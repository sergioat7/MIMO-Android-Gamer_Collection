package es.upsa.mimo.gamercollection.ui.sagadetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import javax.inject.Inject

@HiltViewModel
class SagaDetailViewModel @Inject constructor(
    state: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private var sagaId: Int = state["sagaId"] ?: 0
    private val _sagaDetailLoading = MutableLiveData<Boolean>()
    private val _sagaDetailSuccessMessage = MutableLiveData<Int>()
    private val _sagaDetailError = MutableLiveData<ErrorModel?>()
    private val _saga = MutableLiveData<Saga?>()
    //endregion

    //region Public properties
    val games: List<Game>
        get() = gameRepository.getGamesDatabase()
    val sagaDetailLoading: LiveData<Boolean> = _sagaDetailLoading
    val sagaDetailSuccessMessage: LiveData<Int> = _sagaDetailSuccessMessage
    val sagaDetailError: LiveData<ErrorModel?> = _sagaDetailError
    val saga: LiveData<Saga?> = _saga
    //endregion

    //region Lifecycle methods
    init {

        if (sagaId >= 0) {

            _sagaDetailLoading.value = true
            _saga.value = sagaRepository.getSagaDatabase(sagaId)
            _sagaDetailLoading.value = false
        } else {
            _saga.value = null
        }
    }
    //endregion

    //region Public methods
    fun getOrderedGames(games: List<Game>): List<Game> {

        return when (SharedPreferencesHelper.sortParam) {
            "platform" -> games.sortedBy { it.platform }
            "releaseDate" -> games.sortedBy { it.releaseDate }
            "purchaseDate" -> games.sortedBy { it.purchaseDate }
            "price" -> games.sortedBy { it.price }
            "score" -> games.sortedBy { it.score }
            else -> games.sortedBy { it.name }
        }
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

        _saga.value?.let { saga ->

            _sagaDetailLoading.value = true
            sagaRepository.deleteSaga(saga, {

                _sagaDetailLoading.value = false
                _sagaDetailSuccessMessage.value = R.string.saga_removed
            }, {
                _sagaDetailError.value = it
            })
        } ?: run {
            _sagaDetailError.value = null
        }
    }
    //endregion

    //region Private methods
    private fun createSaga(saga: Saga) {

        _sagaDetailLoading.value = true
        sagaRepository.createSaga(saga, { newSagaCreated ->

            newSagaCreated?.let {
                gameRepository.updateSagaGames(it)
            }

            _sagaDetailLoading.value = false
            _sagaDetailSuccessMessage.value = R.string.saga_created
        }, {
            _sagaDetailError.value = it
        })
    }

    private fun setSaga(saga: Saga) {

        _sagaDetailLoading.value = true
        sagaRepository.setSaga(saga, {

            gameRepository.removeSagaFromGames(saga)
            gameRepository.updateSagaGames(saga)

            _saga.value = it
            _sagaDetailLoading.value = false
        }, {
            _sagaDetailError.value = it
        })
    }
    //endregion
}