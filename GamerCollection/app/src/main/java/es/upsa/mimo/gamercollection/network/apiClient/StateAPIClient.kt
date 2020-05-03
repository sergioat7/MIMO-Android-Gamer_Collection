package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.network.apiService.StateAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import java.util.*
import kotlin.collections.HashMap

class StateAPIClient(
    private val resources: Resources
) {

    fun getStates(success: (List<StateResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        val api = APIClient.getRetrofit().create(StateAPIService::class.java)
        val request = api.getStates(headers)

        APIClient.sendServer<List<StateResponse>, ErrorResponse>(resources, request, { states ->
            success(states)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }
}