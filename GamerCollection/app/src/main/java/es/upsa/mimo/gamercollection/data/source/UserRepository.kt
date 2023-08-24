package es.upsa.mimo.gamercollection.data.source

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.source.di.MainDispatcher
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.network.interfaces.UserApiService
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApiService,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    //endregion

    //region Public methods
    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        val userData = SharedPreferencesHelper.userData
        if (username == Constants.GOOGLE_USER_TEST && password == Constants.GOOGLE_PASSWORD_TEST) {
            SharedPreferencesHelper.userData = UserData(
                Constants.GOOGLE_USER_TEST,
                Constants.GOOGLE_PASSWORD_TEST,
                false
            )
            success("-")
        } else if (userData.username.isEmpty() || userData.username != username) {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.username_not_exist))
        } else if (userData.username == username && userData.password == password) {
            success("-")
        } else {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.wrong_password))
        }
//        externalScope.launch {
//
//            val body = LoginCredentials(username, password)
//
//            try {
//                when (val response = ApiManager.validateResponse(api.login(body))) {
//                    is RequestResult.JsonSuccess -> success(response.body.token)
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        SharedPreferencesHelper.userData = UserData(username, password, false)
        success()
//        externalScope.launch {
//
//            val body = LoginCredentials(username, password)
//
//            try {
//                when (val response = ApiManager.validateResponse(api.register(body))) {
//                    is RequestResult.Success -> success()
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun logout() {
        externalScope.launch {

            try {
                api.logout()
            } catch (e: Exception) {
            }
        }
    }

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        success()
//        externalScope.launch {
//
//            try {
//                val body = NewPassword(password)
//                when (val response = ApiManager.validateResponse(api.updatePassword(body))) {
//                    is RequestResult.Success -> success()
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.deleteUser())) {
//                    is RequestResult.Success -> success()
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }
    //endregion
}