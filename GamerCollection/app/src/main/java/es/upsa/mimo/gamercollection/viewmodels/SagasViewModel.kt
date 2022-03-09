package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class SagasViewModel @Inject constructor(
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private val _sagasLoading = MutableLiveData<Boolean>()
    private val _sagasError = MutableLiveData<ErrorResponse>()
    private val _sagas = MutableLiveData<List<SagaResponse>>()
    private val _originalSagas = MutableLiveData<List<SagaResponse>>()
    private var query: String? = null
    //endregion

    //region Public properties
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.swipeRefresh
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val sagasLoading: LiveData<Boolean> = _sagasLoading
    val sagasError: LiveData<ErrorResponse> = _sagasError
    val sagas: LiveData<List<SagaResponse>> = _sagas
    var expandedIds: MutableList<Int> = mutableListOf()
    //endregion

    //region Public methods
    fun loadSagas() {

        _sagasLoading.value = true
        sagaRepository.loadSagas({

            expandedIds = mutableListOf()
            query = null
            fetchSagas()
            _sagasLoading.value = false
        }, {

            _sagasError.value = it
            _sagasLoading.value = false
        })
    }

    fun fetchSagas() {

        val sagas = sagaRepository.getSagasDatabase().sortedBy { it.name }
        _originalSagas.value = sagas
        _sagas.value = sagas
        if (!query.isNullOrBlank()) {
            _sagas.value = sagas.filter { saga ->
                saga.name?.contains(query ?: Constants.EMPTY_VALUE, true) ?: false
            }
        } else {
            _sagas.value = sagas
        }
    }

    fun searchSagas(query: String) {

        expandedIds = mutableListOf()
        this.query = query
        _sagas.value = _originalSagas.value?.filter { saga ->
            saga.name?.contains(query, true) ?: false
        } ?: listOf()
    }
    //endregion
}