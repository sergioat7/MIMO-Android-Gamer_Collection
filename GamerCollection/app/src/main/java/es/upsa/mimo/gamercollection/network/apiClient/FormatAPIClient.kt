package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.apiService.FormatAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class FormatAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(FormatAPIService::class.java)
    //endregion

    //region Public methods
    fun getFormats(success: (List<FormatResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        val request = api.getFormats(headers)

        APIClient.sendServer(request, success, failure)
    }
    //endregion
}