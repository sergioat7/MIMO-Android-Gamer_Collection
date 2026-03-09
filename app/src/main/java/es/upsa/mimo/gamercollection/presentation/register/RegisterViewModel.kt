package es.upsa.mimo.gamercollection.presentation.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
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
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _registerFormState = MutableStateFlow<LoginFormState?>(null)
    private val _registerLoading = MutableStateFlow<Boolean?>(null)
    private val _registerError = MutableStateFlow<ErrorModel?>(null)
    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    //endregion

    //region Public properties
    val registerFormState: StateFlow<LoginFormState?> = _registerFormState
    val registerLoading: StateFlow<Boolean?> = _registerLoading
    val registerError: StateFlow<ErrorModel?> = _registerError
    val registerSuccess: StateFlow<Boolean?> = _registerSuccess
    //endregion

    //region Public methods
    fun register(username: String, password: String) {
        _registerLoading.value = true
        _registerError.value = null
        userRepository.register(username, password, {
            userRepository.login(username, password, { token ->

                val userData = UserData(username, password, true)
                userRepository.run {
                    storeUserData(userData)
                    storeCredentials(AuthData(token))
                }

                _registerLoading.value = false
                _registerSuccess.value = true
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
        _registerFormState.value = LoginFormState(usernameError, passwordError, isDataValid)
    }
    //endregion
}