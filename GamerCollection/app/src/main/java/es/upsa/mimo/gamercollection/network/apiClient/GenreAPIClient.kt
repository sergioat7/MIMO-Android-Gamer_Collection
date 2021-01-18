package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlin.collections.HashMap

class GenreAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit(sharedPrefHandler).create(GenreAPIService::class.java)

    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        val request = api.getGenres(headers)

        APIClient.sendServer<List<GenreResponse>, ErrorResponse>(sharedPrefHandler, resources, request, {
            success(it)
        }, {
            failure(it)
        })
    }
}