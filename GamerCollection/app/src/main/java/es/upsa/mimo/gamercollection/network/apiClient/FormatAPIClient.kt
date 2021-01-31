package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.apiService.FormatAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class FormatAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    // MARK: - Private properties

    private val api = APIClient.retrofit.create(FormatAPIService::class.java)

    // MARK: - Public methods

    fun getFormats(success: (List<FormatResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getFormats(headers)

        APIClient.sendServer(request, success, failure)
    }
}