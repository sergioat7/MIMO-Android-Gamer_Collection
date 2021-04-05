package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService

class GenreAPIClient {

    //region Private properties
    private val api = ApiManager.getService<GenreAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.getGenres()
        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}