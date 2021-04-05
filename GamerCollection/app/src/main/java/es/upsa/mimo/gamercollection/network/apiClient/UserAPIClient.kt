package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiService.UserAPIService
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

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

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.logout(headers)

        ApiManager.sendServer<Void, ErrorResponse>(request, {}, {})
    }

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val newPasword = NewPassword(password)
        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.updatePassword(headers, newPasword)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.deleteUser(headers)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}