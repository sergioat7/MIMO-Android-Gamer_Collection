package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiService.UserAPIService

class UserAPIClient {

    //region Private properties
    private val api = ApiManager.getService<UserAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val loginCredentials = LoginCredentials(username, password)

        val request = api.login(loginCredentials)
        ApiManager.sendServer(request, {
            success(it.token)
        }, failure)
    }

    fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val loginCredentials = LoginCredentials(username, password)

        val request = api.register(loginCredentials)
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun logout() {

        val request = api.logout()
        ApiManager.sendServer<Void, ErrorResponse>(request, {}, {})
    }

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.updatePassword(NewPassword(password))
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.deleteUser()
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}