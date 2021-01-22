package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.apiService.SagaAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlin.collections.HashMap

class SagaAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.retrofit.create(SagaAPIService::class.java)

    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.getSagas(headers)

        APIClient.sendServer<List<SagaResponse>, ErrorResponse>(request, {
            success(it)
        }, {
            failure(it)
        })
    }

    fun createSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.createSaga(headers, saga)

        APIClient.sendServer<Void, ErrorResponse>(request, {
            success()
        }, {
            failure(it)
        })
    }

    fun setSaga(saga: SagaResponse, success: (SagaResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.setSaga(headers, saga.id, saga)

        APIClient.sendServer<SagaResponse, ErrorResponse>(request, {
            success(it)
        }, {
            failure(it)
        })
    }

    fun deleteSaga(sagaId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.deleteSaga(headers, sagaId)

        APIClient.sendServer<Void, ErrorResponse>(request, {
            success()
        }, {
            failure(it)
        })
    }
}