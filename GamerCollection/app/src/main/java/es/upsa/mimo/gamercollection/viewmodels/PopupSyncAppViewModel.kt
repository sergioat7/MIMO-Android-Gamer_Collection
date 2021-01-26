package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.*
import javax.inject.Inject

class PopupSyncAppViewModel @Inject constructor(
    private val formatAPIClient: FormatAPIClient,
    private val gameAPIClient: GameAPIClient,
    private val genreAPIClient: GenreAPIClient,
    private val platformAPIClient: PlatformAPIClient,
    private val sagaAPIClient: SagaAPIClient,
    private val stateAPIClient: StateAPIClient,
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

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->
                        gameAPIClient.getGames({ games ->
                            sagaAPIClient.getSagas({ sagas ->

                                formatRepository.manageFormats(formats)
                                genreRepository.manageGenres(genres)
                                platformRepository.managePlatforms(platforms)
                                stateRepository.manageStates(states)
                                gameRepository.manageGames(games)
                                sagaRepository.manageSagas(sagas)

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