package es.upsa.mimo.gamercollection.presentation.modals.syncapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import javax.inject.Inject

@HiltViewModel
class PopupSyncAppViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private val _popupSyncAppError = MutableLiveData<ErrorModel?>()
    //endregion

    //region Public properties
    val popupSyncAppError: LiveData<ErrorModel?> = _popupSyncAppError
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