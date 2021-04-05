package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class GenreAPIClient {

    //region Private properties
    private val api = ApiManager.getService<GenreAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        val request = api.getGenres(headers)

        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}