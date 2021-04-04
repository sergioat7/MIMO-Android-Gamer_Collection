package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiService.UserAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class UserAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    //region Private properties
    private val api = APIClient.retrofit.create(UserAPIService::class.java)
    //endregion

    //region Public methods
    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val loginCredentials = LoginCredentials(username, password)

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.login(headers, loginCredentials)

        APIClient.sendServer(request, {
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

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.register(headers, loginCredentials)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun logout() {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.logout(headers)

        APIClient.sendServer<Void, ErrorResponse>(request, {}, {})
    }

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val newPasword = NewPassword(password)
        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.updatePassword(headers, newPasword)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.deleteUser(headers)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}