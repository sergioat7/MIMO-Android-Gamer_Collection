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

    private val api = APIClient.getRetrofit().create(SagaAPIService::class.java)

    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.getSagas(headers)

        APIClient.sendServer<List<SagaResponse>, ErrorResponse>(resources, request, { sagas ->
            success(sagas)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }

    fun createSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.createSaga(headers, saga)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, { errorResponse ->
            failure(errorResponse)
        })
    }

    fun setSaga(saga: SagaResponse, success: (SagaResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.setSaga(headers, saga.id, saga)

        APIClient.sendServer<SagaResponse, ErrorResponse>(resources, request, { game ->
            success(game)
        }, { errorResponse ->
            failure(errorResponse)
    }

    fun deleteSaga(sagaId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.deleteSaga(headers, sagaId)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, {
            failure(it)
        })
    }
}