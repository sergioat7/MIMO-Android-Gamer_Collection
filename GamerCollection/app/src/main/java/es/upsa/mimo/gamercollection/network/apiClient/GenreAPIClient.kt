package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class GenreAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    // MARK: - Private properties

    private val api = APIClient.retrofit.create(GenreAPIService::class.java)

    // MARK: - Public methods

    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getGenres(headers)

        APIClient.sendServer(request, success, failure)
    }
}