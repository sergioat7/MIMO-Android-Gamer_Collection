package es.upsa.mimo.gamercollection.presentation.sagas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SagasViewModel @Inject constructor(
    private val sagaRepository: SagaRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _sagasLoading = MutableStateFlow<Boolean?>(null)
    private val _sagasError = MutableStateFlow<ErrorModel?>(null)
    private val _sagas = MutableStateFlow<List<Saga>>(emptyList())
    private val originalSagas = MutableStateFlow<List<Saga>>(emptyList())
    private var query: String? = null
    //endregion

    //region Public properties
    val swipeRefresh: Boolean
        get() = userRepository.swipeRefresh
    val sagasLoading: StateFlow<Boolean?> = _sagasLoading
    val sagasError: StateFlow<ErrorModel?> = _sagasError
    val sagas: StateFlow<List<Saga>> = _sagas
    var expandedIds: MutableList<Int> = mutableListOf()
    //endregion

    //region Public methods
    fun loadSagas() {
        _sagasLoading.value = true
        expandedIds = mutableListOf()
        query = null
        fetchSagas()
        _sagasLoading.value = false
    }

    fun fetchSagas() {
        viewModelScope.launch {
            val sagas = sagaRepository.getSagasDatabase().sortedBy { it.name }
            originalSagas.value = sagas
            _sagas.value = sagas
            if (!query.isNullOrBlank()) {
                _sagas.value = sagas.filter { saga ->
                    saga.name?.contains(query ?: Constants.EMPTY_VALUE, true) ?: false
                }
            } else {
                _sagas.value = sagas
            }
        }
    }

    fun searchSagas(query: String) {
        expandedIds = mutableListOf()
        this.query = query
        _sagas.value = originalSagas.value.filter { saga ->
            saga.name?.contains(query, true) ?: false
        } ?: listOf()
    }
    //endregion
}