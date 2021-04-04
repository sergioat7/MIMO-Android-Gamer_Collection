package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.network.apiService.SagaAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class SagaAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(SagaAPIService::class.java)
    //endregion

    //region Public methods
    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.getSagas(headers)

        APIClient.sendServer(request, success, failure)
    }

    fun createSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.createSaga(headers, saga)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.setSaga(headers, saga.id, saga)

        APIClient.sendServer(request, success, failure)
    }

    fun deleteSaga(sagaId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.deleteSaga(headers, sagaId)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}