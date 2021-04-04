package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.login.LoginFormState
import es.upsa.mimo.gamercollection.models.login.UserData
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiClient.UserAPIClient
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
) : ViewModel() {

    //region Private properties
    private val userAPIClient = UserAPIClient()
    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _loginLoading = MutableLiveData<Boolean>()
    private val _loginError = MutableLiveData<ErrorResponse>()
    //endregion

    //region Public properties
    val username: String
        get() = SharedPreferencesHandler.getUserData().username
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginLoading: LiveData<Boolean> = _loginLoading
    val loginError: LiveData<ErrorResponse> = _loginError
    //endregion

    //region Public methods
    fun login(username: String, password: String) {

        _loginLoading.value = true
        userAPIClient.login(username, password, { token ->

            val userData = UserData(username, password, false)
            val authData = AuthData(token)
            SharedPreferencesHandler.run {
                storeUserData(userData)
                storeCredentials(authData)
            }
            loadContent(userData)
        }, {
            _loginError.value = it
        })
    }

    fun loginDataChanged(username: String, password: String) {

        var usernameError: Int? = null
        var passwordError: Int? = null
        var isDataValid = true

        if (!Constants.isUserNameValid(username)) {
            usernameError = R.string.invalid_username
            isDataValid = false
        }
        if (!Constants.isPasswordValid(password)) {
            passwordError = R.string.invalid_password
            isDataValid = false
        }
        _loginForm.value = LoginFormState(usernameError, passwordError, isDataValid)
    }
    //endregion

    //region Private methods
    private fun loadContent(userData: UserData) {

        formatRepository.loadFormats({
            gameRepository.loadGames({
                genreRepository.loadGenres({
                    platformRepository.loadPlatforms({
                        sagaRepository.loadSagas({
                            stateRepository.loadStates({

                                userData.isLoggedIn = true
                                SharedPreferencesHandler.storeUserData(userData)

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
    //endregion
}