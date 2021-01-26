package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class SagaDetailViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val sagaAPIClient: SagaAPIClient,
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private var sagaId: Int? = null
    private val _sagaDetailLoading = MutableLiveData<Boolean>()
    private val _sagaDetailError = MutableLiveData<ErrorResponse>()
    private val _saga = MutableLiveData<SagaResponse?>()

    //MARK: - Public properties

    val games: List<GameResponse> = gameRepository.getGames()
    val platforms: List<PlatformResponse> = platformRepository.getPlatforms()
    val states: List<StateResponse> = stateRepository.getStates()
    val sagaDetailLoading: LiveData<Boolean> = _sagaDetailLoading
    val sagaDetailError: LiveData<ErrorResponse> = _sagaDetailError
    val saga: LiveData<SagaResponse?> = _saga

    //MARK: - Public methods

    fun getSaga() {

        sagaId?.let {

            _sagaDetailLoading.value = true
            _saga.value = sagaRepository.getSaga(it)
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

            sagaAPIClient.setSaga(newSaga, {
                sagaRepository.updateSaga(it)
                removeSagaFromGames(newSaga)
                updateGames(newSaga)

                _saga.value = it
                _sagaDetailLoading.value = false
            }, {
                _sagaDetailError.value = it
            })
        } else {

            sagaAPIClient.createSaga(newSaga, {
                sagaAPIClient.getSagas({ sagas ->

                    for (saga in sagas) {
                        sagaRepository.insertSaga(saga)
                    }

                    val newSagaCreated = sagas.firstOrNull { saga ->
                        val game = saga.games.firstOrNull { game ->
                            game.id == newSaga.games.firstOrNull()?.id
                        }
                        game != null
                    }
                    newSagaCreated?.let {
                        updateGames(it)
                    }

                    _sagaDetailLoading.value = false
                    _sagaDetailError.value = null
                }, {
                    _sagaDetailError.value = it
                })
            }, {
                _sagaDetailError.value = it
            })
        }
    }

    fun deleteSaga() {

        _saga.value?.let { saga ->

            _sagaDetailLoading.value = true
            sagaAPIClient.deleteSaga(saga.id, {
                sagaRepository.deleteSaga(saga)

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

    //MARK: - Private methods

    private fun removeSagaFromGames(saga: SagaResponse) {

        val newSagaGames = saga.games
        val oldSagaGames = this.games.filter { it.saga?.id  == saga.id }
        for (oldSagaGame in oldSagaGames) {
            if (newSagaGames.firstOrNull { it.id == oldSagaGame.id } == null) {

                oldSagaGame.saga = null
                gameRepository.updateGame(oldSagaGame)
            }
        }

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newSagaGame in newSagaGames) {

            newSagaGame.saga = sagaVar
            gameRepository.updateGame(newSagaGame)
        }
    }

    private fun updateGames(saga: SagaResponse) {

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newGame in saga.games) {

            newGame.saga = sagaVar
            gameRepository.updateGame(newGame)
        }
    }
}