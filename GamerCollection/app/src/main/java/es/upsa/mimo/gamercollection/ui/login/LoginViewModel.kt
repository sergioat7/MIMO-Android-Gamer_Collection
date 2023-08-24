package es.upsa.mimo.gamercollection.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.LoginFormState
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.data.source.GameRepository
import es.upsa.mimo.gamercollection.data.source.SagaRepository
import es.upsa.mimo.gamercollection.data.source.UserRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.data.source.SharedPreferencesHelper
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //region Private properties
    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _loginLoading = MutableLiveData<Boolean>()
    private val _loginError = MutableLiveData<ErrorResponse?>()
    //endregion

    //region Public properties
    val username: String
        get() = SharedPreferencesHelper.userData.username
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginLoading: LiveData<Boolean> = _loginLoading
    val loginError: LiveData<ErrorResponse?> = _loginError
    //endregion

    //region Public methods
    fun login(username: String, password: String) {

        _loginLoading.value = true
        userRepository.login(username, password, { token ->

            val userData = UserData(username, password, false)
            SharedPreferencesHelper.run {
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
        if (!Constants.isPasswordValid(password)) {
            passwordError = R.string.invalid_password
            isDataValid = false
        }
        _loginForm.value = LoginFormState(usernameError, passwordError, isDataValid)
    }
    //endregion

    //region Private methods
    private fun loadContent(userData: UserData) {

//        gameRepository.loadGames({
//            sagaRepository.loadSagas({

                userData.isLoggedIn = true
                SharedPreferencesHelper.userData = userData

                _loginError.value = null
                _loginLoading.value = false
//            }, {
//                _loginError.value = it
//            })
//        }, {
//            _loginError.value = it
//        })
    }
    //endregion
}