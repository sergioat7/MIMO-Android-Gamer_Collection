package es.upsa.mimo.gamercollection.presentation.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.data.local.model.AuthData
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.presentation.login.model.LoginFormState
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferences: SharedPreferencesHelper,
) : ViewModel() {

    //region Private properties
    private val _loginFormState = MutableStateFlow<LoginFormState?>(null)
    private val _loginLoading = MutableStateFlow<Boolean?>(null)
    private val _loginError = MutableStateFlow<ErrorModel?>(null)
    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    //endregion

    //region Public properties
    val username: String
        get() = preferences.userData.username
    val loginFormState: StateFlow<LoginFormState?> = _loginFormState
    val loginLoading: StateFlow<Boolean?> = _loginLoading
    val loginError: StateFlow<ErrorModel?> = _loginError
    val loginSuccess: StateFlow<Boolean?> = _loginSuccess
    //endregion

    //region Public methods
    fun login(username: String, password: String) {
        _loginLoading.value = true
        _loginError.value = null
        userRepository.login(username, password, { token ->

            val userData = UserData(username, password, false)
            preferences.run {
                this.userData = userData
                this.credentials = AuthData(token)
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
        if (password.length <= 3) {
            passwordError = R.string.invalid_password_old
            isDataValid = false
        }
        _loginFormState.value = LoginFormState(usernameError, passwordError, isDataValid)
    }
    //endregion

    //region Private methods
    private fun loadContent(userData: UserData) {
        userData.isLoggedIn = true
        preferences.userData = userData

        _loginSuccess.value = true
        _loginLoading.value = false
    }
    //endregion
}