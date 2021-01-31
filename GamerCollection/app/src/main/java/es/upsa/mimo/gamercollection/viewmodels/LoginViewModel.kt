package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.login.UserData
import es.upsa.mimo.gamercollection.network.apiClient.UserAPIClient
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val userAPIClient: UserAPIClient,
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _loginLoading = MutableLiveData<Boolean>()
    private val _loginError = MutableLiveData<ErrorResponse>()

    //MARK: - Public properties

    val username: String
        get() = sharedPreferencesHandler.getUserData().username
    val loginLoading: LiveData<Boolean> = _loginLoading
    val loginError: LiveData<ErrorResponse> = _loginError

    //MARK: - Public methods

    fun login(username: String, password: String) {

        _loginLoading.value = true
        userAPIClient.login(username, password, { token ->

            val userData = UserData(username, password, false)
            val authData = AuthData(token)
            sharedPreferencesHandler.run {
                storeUserData(userData)
                storeCredentials(authData)
            }
            loadContent(userData)
        }, {
            _loginError.value = it
        })
    }

    //MARK: - Private methods

    private fun loadContent(userData: UserData) {

        formatRepository.loadFormats({
            gameRepository.loadGames({
                genreRepository.loadGenres({
                    platformRepository.loadPlatforms({
                        sagaRepository.loadSagas({
                            stateRepository.loadStates({

                                userData.isLoggedIn = true
                                sharedPreferencesHandler.storeUserData(userData)

                                _loginError.value = null
                                _loginLoading.value = false
                            }, {
                                _loginError.value = it
                            })
                        }, {
                            _loginError.value = it
                        })
                    }, {
                        _loginError.value = it
                    })
                }, {
                    _loginError.value = it
                })
            }, {
                _loginError.value = it
            })
        }, {
            _loginError.value = it
        })
    }
}