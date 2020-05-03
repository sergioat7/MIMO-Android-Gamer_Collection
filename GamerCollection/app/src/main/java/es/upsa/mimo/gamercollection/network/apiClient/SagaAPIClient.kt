package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.apiService.SagaAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import kotlin.collections.HashMap

class SagaAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val api = APIClient.getRetrofit().create(SagaAPIService::class.java)
        val request = api.getSagas(headers)

        APIClient.sendServer<List<SagaResponse>, ErrorResponse>(resources, request, { sagas ->
            success(sagas)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }
}