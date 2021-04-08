package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.PlatformApiService

class PlatformAPIClient {

    //region Private properties
    private val api = ApiManager.getService<PlatformApiService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getPlatforms(success: (List<PlatformResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.getPlatforms()
        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}