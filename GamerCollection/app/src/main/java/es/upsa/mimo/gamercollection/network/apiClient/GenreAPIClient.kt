package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler

class GenreAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(GenreAPIService::class.java)
    //endregion

    //region Public methods
    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        val request = api.getGenres(headers)

        APIClient.sendServer(request, success, failure)
    }
    //endregion
}