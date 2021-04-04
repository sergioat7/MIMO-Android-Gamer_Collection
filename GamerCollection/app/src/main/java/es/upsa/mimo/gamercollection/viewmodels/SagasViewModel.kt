package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class SagasViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private val _sagasLoading = MutableLiveData<Boolean>()
    private val _sagasError = MutableLiveData<ErrorResponse>()
    private val _sagas = MutableLiveData<List<SagaResponse>>()
    //endregion

    //region Public properties
    val swipeRefresh: Boolean
        get() = sharedPreferencesHandler.getSwipeRefresh()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val sagasLoading: LiveData<Boolean> = _sagasLoading
    val sagasError: LiveData<ErrorResponse> = _sagasError
    val sagas: LiveData<List<SagaResponse>> = _sagas
    var expandedIds: MutableList<Int> = mutableListOf()
    //endregion

    //region Public methods
    fun getSagas() {
        _sagas.value = sagaRepository.getSagasDatabase().sortedBy { it.name }
    }

    fun loadSagas() {

        _sagasLoading.value = true
        sagaRepository.loadSagas({

            getSagas()
            _sagasLoading.value = false
        }, {

            _sagasError.value = it
            _sagasLoading.value = false
        })
    }
    //endregion
}