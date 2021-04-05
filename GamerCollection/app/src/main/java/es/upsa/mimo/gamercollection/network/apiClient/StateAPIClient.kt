package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.network.apiService.StateAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class StateAPIClient {

    //region Private properties
    private val api = ApiManager.getService<StateAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getStates(success: (List<StateResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        val request = api.getStates(headers)

        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}