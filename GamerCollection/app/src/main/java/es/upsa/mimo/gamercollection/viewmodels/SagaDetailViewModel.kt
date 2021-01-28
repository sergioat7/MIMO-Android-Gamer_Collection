package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class SagaDetailViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private var sagaId: Int? = null
    private val _sagaDetailLoading = MutableLiveData<Boolean>()
    private val _sagaDetailError = MutableLiveData<ErrorResponse>()
    private val _saga = MutableLiveData<SagaResponse?>()

    //MARK: - Public properties

    val games: List<GameResponse>
        get() = gameRepository.getGamesDatabase()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val states: List<StateResponse>
        get() = stateRepository.getStatesDatabase()
    val sagaDetailLoading: LiveData<Boolean> = _sagaDetailLoading
    val sagaDetailError: LiveData<ErrorResponse> = _sagaDetailError
    val saga: LiveData<SagaResponse?> = _saga

    //MARK: - Public methods

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

        return when(sharedPreferencesHandler.getSortingKey()) {
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

        _sagaDetailLoading.value = true
        if (_saga.value != null) {

            sagaRepository.setSaga(newSaga, {

                gameRepository.removeSagaFromGames(newSaga)
                gameRepository.updateSagaGames(newSaga)

                _saga.value = it
                _sagaDetailLoading.value = false
            }, {
                _sagaDetailError.value = it
            })
        } else {

            sagaRepository.createSaga(newSaga, { newSagaCreated ->

                newSagaCreated?.let {
                    gameRepository.updateSagaGames(it)
                }

                _sagaDetailLoading.value = false
                _sagaDetailError.value = null
            }, {
                _sagaDetailError.value = it
            })
        }
    }

    fun deleteSaga() {

        _saga.value?.let { saga ->

            _sagaDetailLoading.value = true
            sagaRepository.deleteSaga(saga, {

                _sagaDetailLoading.value = false
                _sagaDetailError.value = null
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
}