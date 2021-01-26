package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiService.PlatformAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class PlatformAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    // MARK: - Private properties

    private val api = APIClient.retrofit.create(PlatformAPIService::class.java)

    // MARK: - Public methods

    fun getPlatforms(success: (List<PlatformResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getPlatforms(headers)

        APIClient.sendServer<List<PlatformResponse>, ErrorResponse>(request, {
            success(it)
        }, {
            failure(it)
        })
    }
}