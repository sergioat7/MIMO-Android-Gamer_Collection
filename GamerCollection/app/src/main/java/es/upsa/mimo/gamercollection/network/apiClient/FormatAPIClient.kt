package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.apiService.FormatAPIService

class FormatAPIClient {

    //region Private properties
    private val api = ApiManager.getService<FormatAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getFormats(success: (List<FormatResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.getFormats()
        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}