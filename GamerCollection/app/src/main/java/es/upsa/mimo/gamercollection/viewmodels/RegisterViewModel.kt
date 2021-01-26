package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val formatAPIClient: FormatAPIClient,
    private val genreAPIClient: GenreAPIClient,
    private val platformAPIClient: PlatformAPIClient,
    private val stateAPIClient: StateAPIClient,
    private val userAPIClient: UserAPIClient,
    private val formatRepository: FormatRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _registerLoading = MutableLiveData<Boolean>()
    private val _registerError = MutableLiveData<ErrorResponse>()

    //MARK: - Public properties

    val registerLoading: LiveData<Boolean> = _registerLoading
    val registerError: LiveData<ErrorResponse> = _registerError

    //MARK: - Public methods

    fun register(username: String, password: String) {

        _registerLoading.value = true
        userAPIClient.register(username, password, {
            userAPIClient.login(username, password, { token ->

                val userData = UserData(username, password, false)
                val authData = AuthData(token)
                sharedPreferencesHandler.run {
                    storeUserData(userData)
                    storeCredentials(authData)
                }
                loadContent(userData)
            }, {
                _registerError.value = it
            })
        }, {
            _registerError.value = it
        })
    }

    //MARK: - Private methods

    private fun loadContent(userData: UserData) {

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->

                        formatRepository.manageFormats(formats)
                        genreRepository.manageGenres(genres)
                        platformRepository.managePlatforms(platforms)
                        stateRepository.manageStates(states)

                        userData.isLoggedIn = true
                        sharedPreferencesHandler.storeUserData(userData)

                        _registerLoading.value = false
                        _registerError.value = null
                    }, {
                        _registerError.value = it
                    })
                }, {
                    _registerError.value = it
                })
            }, {
                _registerError.value = it
            })
        }, {
            _registerError.value = it
        })
    }
}