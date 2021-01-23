package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.network.apiService.StateAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class StateAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.retrofit.create(StateAPIService::class.java)

    fun getStates(success: (List<StateResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getStates(headers)

        APIClient.sendServer<List<StateResponse>, ErrorResponse>(request, {
            success(it)
        }, {
            failure(it)
        })
    }
}