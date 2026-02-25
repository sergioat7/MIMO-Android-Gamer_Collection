package es.upsa.mimo.gamercollection.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.model.AuthData
import es.upsa.mimo.gamercollection.presentation.login.model.LoginFormState
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
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
    private val _loginError = MutableLiveData<ErrorModel?>()
    //endregion

    //region Public properties
    val username: String
        get() = SharedPreferencesHelper.userData.username
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginLoading: LiveData<Boolean> = _loginLoading
    val loginError: LiveData<ErrorModel?> = _loginError
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
        if (password.length <= 3) {
            passwordError = R.string.invalid_password_old
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