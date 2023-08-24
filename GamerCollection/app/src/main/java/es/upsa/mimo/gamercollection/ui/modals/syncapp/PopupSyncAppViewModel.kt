package es.upsa.mimo.gamercollection.ui.modals.syncapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.data.source.GameRepository
import es.upsa.mimo.gamercollection.data.source.SagaRepository
import javax.inject.Inject

@HiltViewModel
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