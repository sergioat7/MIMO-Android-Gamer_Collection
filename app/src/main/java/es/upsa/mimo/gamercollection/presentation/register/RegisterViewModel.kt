package es.upsa.mimo.gamercollection.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _registerFormState = MutableLiveData<LoginFormState>()
    private val _registerLoading = MutableLiveData<Boolean>()
    private val _registerError = MutableLiveData<ErrorModel?>()
    //endregion

    //region Public properties
    val registerFormState: LiveData<LoginFormState> = _registerFormState
    val registerLoading: LiveData<Boolean> = _registerLoading
    val registerError: LiveData<ErrorModel?> = _registerError
    //endregion

    //region Public methods
    fun register(username: String, password: String) {
        _registerLoading.value = true
        userRepository.register(username, password, {
            userRepository.login(username, password, { token ->

                val userData = UserData(username, password, true)
                SharedPreferencesHelper.run {
                    this.userData = userData
                    this.credentials = AuthData(token)
                }

                _registerLoading.value = false
                _registerError.value = null
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