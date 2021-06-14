package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class SagaDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private var sagaId: Int? = null
    private val _sagaDetailLoading = MutableLiveData<Boolean>()
    private val _sagaDetailSuccessMessage = MutableLiveData<Int>()
    private val _sagaDetailError = MutableLiveData<ErrorResponse?>()
    private val _saga = MutableLiveData<SagaResponse?>()
    //endregion

    //region Public properties
    val games: List<GameResponse>
        get() = gameRepository.getGamesDatabase()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val sagaDetailLoading: LiveData<Boolean> = _sagaDetailLoading
    val sagaDetailSuccessMessage: LiveData<Int> = _sagaDetailSuccessMessage
    val sagaDetailError: LiveData<ErrorResponse?> = _sagaDetailError
    val saga: LiveData<SagaResponse?> = _saga
    //endregion

    //region Public methods
    fun getSaga() {

        sagaId?.let {

            _sagaDetailLoading.value = true
            _saga.value = sagaRepository.getSagaDatabase(it)
            _sagaDetailLoading.value = false
        } ?: run {
            _saga.value = null
        }
    }

    fun getOrderedGames(games: List<GameResponse>): List<GameResponse> {

        return when (SharedPreferencesHelper.getSortingKey()) {
            "platform" -> games.sortedBy { it.platform }
            "releaseDate" -> games.sortedBy { it.releaseDate }
            "purchaseDate" -> games.sortedBy { it.purchaseDate }
            "price" -> games.sortedBy { it.price }
            "score" -> games.sortedBy { it.score }
            else -> games.sortedBy { it.name }
        }
    }

    fun saveSaga(name: String, games: List<GameResponse>) {

        val newSaga = SagaResponse(
            sagaId ?: 0,
            name,
            games
        )

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

    fun setSagaId(sagaId: Int?) {
        this.sagaId = sagaId
    }
    //endregion

    //region Private methods
    private fun createSaga(saga: SagaResponse) {

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

    private fun setSaga(saga: SagaResponse) {

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