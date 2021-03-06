package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.network.apiService.FormatAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlin.collections.HashMap

class FormatAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit(sharedPrefHandler).create(FormatAPIService::class.java)

    fun getFormats(success: (List<FormatResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        val request = api.getFormats(headers)

        APIClient.sendServer<List<FormatResponse>, ErrorResponse>(sharedPrefHandler, resources, request, {
            success(it)
        }, {
            failure(it)
        })
    }
}