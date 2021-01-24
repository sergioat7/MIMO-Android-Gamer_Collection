package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class SagasViewModel @Inject constructor(
    sharedPreferencesHandler: SharedPreferencesHandler,
    private val sagaAPIClient: SagaAPIClient,
    platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _sagasLoading = MutableLiveData<Boolean>()
    private val _sagasError = MutableLiveData<ErrorResponse>()
    private val _sagas = MutableLiveData<List<SagaResponse>>()

    //MARK: - Public properties

    val swipeRefresh: Boolean = sharedPreferencesHandler.getSwipeRefresh()
    val platforms: List<PlatformResponse> = platformRepository.getPlatforms()
    val states: List<StateResponse> = stateRepository.getStates()
    val sagasLoading: LiveData<Boolean> = _sagasLoading
    val sagasError: LiveData<ErrorResponse> = _sagasError
    val sagas: LiveData<List<SagaResponse>> = _sagas

    //MARK: - Public methods

    fun getSagas() {
        _sagas.value = sagaRepository.getSagas().sortedBy { it.name }
    }

    fun loadSagas() {

        sagaAPIClient.getSagas({

            sagaRepository.manageSagas(it)
            _sagas.value = sagaRepository.getSagas()
            _sagasLoading.value = false
        },{

            _sagasError.value = it
            _sagasLoading.value = false
        })
    }
}