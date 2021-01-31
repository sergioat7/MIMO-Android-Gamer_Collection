package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.repositories.*
import javax.inject.Inject

class PopupSyncAppViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _popupSyncAppError = MutableLiveData<ErrorResponse>()

    //MARK: - Public properties

    val popupSyncAppError: LiveData<ErrorResponse> = _popupSyncAppError

    //MARK: - Public methods

    fun loadContent() {

        formatRepository.loadFormats({
            gameRepository.loadGames({
                genreRepository.loadGenres({
                    platformRepository.loadPlatforms({
                        sagaRepository.loadSagas({
                            stateRepository.loadStates({

                                _popupSyncAppError.value = null
                            }, {
                                _popupSyncAppError.value = it
                            })
                        }, {
                            _popupSyncAppError.value = it
                        })
                    }, {
                        _popupSyncAppError.value = it
                    })
                }, {
                    _popupSyncAppError.value = it
                })
            }, {
                _popupSyncAppError.value = it
            })
        }, {
            _popupSyncAppError.value = it
        })
    }
}