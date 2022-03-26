package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import javax.inject.Inject

class PopupSyncAppViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private val _popupSyncAppError = MutableLiveData<ErrorResponse?>()
    //endregion

    //region Public properties
    val popupSyncAppError: LiveData<ErrorResponse?> = _popupSyncAppError
    //endregion

    //region Public methods
    fun loadContent() {

        gameRepository.loadGames({
            sagaRepository.loadSagas({

                _popupSyncAppError.value = null
            }, {
                _popupSyncAppError.value = it
            })
        }, {
            _popupSyncAppError.value = it
        })
    }
    //endregion
}