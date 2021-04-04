package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.network.apiService.StateAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class StateAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    //region Private properties
    private val api = APIClient.retrofit.create(StateAPIService::class.java)
    //endregion

    //region Public methods
    fun getStates(success: (List<StateResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getStates(headers)

        APIClient.sendServer(request, success, failure)
    }
    //endregion
}