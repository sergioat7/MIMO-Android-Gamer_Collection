package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiService.PlatformAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class PlatformAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(PlatformAPIService::class.java)
    //endregion

    //region Public methods
    fun getPlatforms(success: (List<PlatformResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        val request = api.getPlatforms(headers)

        APIClient.sendServer(request, success, failure)
    }
    //endregion
}