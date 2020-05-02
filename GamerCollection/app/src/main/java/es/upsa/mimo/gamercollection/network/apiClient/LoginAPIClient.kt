package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.LoginCredentials
import es.upsa.mimo.gamercollection.models.LoginResponse
import es.upsa.mimo.gamercollection.network.apiService.UserAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import java.util.*
import kotlin.collections.HashMap

class LoginAPIClient {
    companion object{

        fun login(username: String, password: String, resources: Resources, success: (String) -> Unit, failure: (ErrorResponse) -> Unit) {

            val loginCredentials = LoginCredentials(username, password)

            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
            val api = APIClient.getRetrofit().create(UserAPIService::class.java)
            val request = api.login(headers, loginCredentials)

            APIClient.sendServer<LoginResponse, ErrorResponse>(resources, request, { response ->
                success(response.token)
            }, { errorResponse ->
                failure(errorResponse)
            })
        }
    }
}