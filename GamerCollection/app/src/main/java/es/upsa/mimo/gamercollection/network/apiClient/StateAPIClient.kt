package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.network.apiService.StateAPIService

class StateAPIClient {

    //region Private properties
    private val api = ApiManager.getService<StateAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getStates(success: (List<StateResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.getStates()
        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}