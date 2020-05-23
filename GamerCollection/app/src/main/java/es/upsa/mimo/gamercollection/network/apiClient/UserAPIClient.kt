package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.LoginCredentials
import es.upsa.mimo.gamercollection.models.LoginResponse
import es.upsa.mimo.gamercollection.models.NewPassword
import es.upsa.mimo.gamercollection.network.apiService.UserAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import kotlin.collections.HashMap

class UserAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit().create(UserAPIService::class.java)

    fun login(username: String, password: String, success: (String) -> Unit, failure: (ErrorResponse) -> Unit) {

        val loginCredentials = LoginCredentials(username, password)

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        val request = api.login(headers, loginCredentials)

        APIClient.sendServer<LoginResponse, ErrorResponse>(resources, request, {
            success(it.token)
        }, {
            failure(it)
        })
    }

    fun register(username: String, password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val loginCredentials = LoginCredentials(username, password)

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        val request = api.register(headers, loginCredentials)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, {
            failure(it)
        })
    }

    fun logout(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.logout(headers)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, {
            failure(it)
        })
    }

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val newPasword = NewPassword(password)
        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.updatePassword(headers, newPasword)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, {
            failure(it)
        })
    }

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.deleteUser(headers)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, {
            failure(it)
        })
    }
}