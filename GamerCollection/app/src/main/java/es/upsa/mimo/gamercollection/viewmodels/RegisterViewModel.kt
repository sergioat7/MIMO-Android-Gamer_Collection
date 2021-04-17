package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.login.LoginFormState
import es.upsa.mimo.gamercollection.models.login.UserData
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val stateRepository: StateRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //region Private properties
    private val _registerForm = MutableLiveData<LoginFormState>()
    private val _registerLoading = MutableLiveData<Boolean>()
    private val _registerError = MutableLiveData<ErrorResponse>()
    //endregion

    //region Public properties
    val registerFormState: LiveData<LoginFormState> = _registerForm
    val registerLoading: LiveData<Boolean> = _registerLoading
    val registerError: LiveData<ErrorResponse> = _registerError
    //endregion

    //region Public methods
    fun register(username: String, password: String) {

        _registerLoading.value = true
        userRepository.register(username, password, {
            userRepository.login(username, password, { token ->

                val userData = UserData(username, password, false)
                val authData = AuthData(token)
                SharedPreferencesHelper.run {
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

    fun registerDataChanged(username: String, password: String, confirmPassword: String) {

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
        if (password != confirmPassword) {
            passwordError = R.string.invalid_repeat_password
            isDataValid = false
        }
        _registerForm.value = LoginFormState(usernameError, passwordError, isDataValid)
    }
    //endregion

    //region Private methods
    private fun loadContent(userData: UserData) {

        formatRepository.loadFormats({
            genreRepository.loadGenres({
                platformRepository.loadPlatforms({
                    stateRepository.loadStates({

                        userData.isLoggedIn = true
                        SharedPreferencesHelper.storeUserData(userData)

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
    //endregion
}