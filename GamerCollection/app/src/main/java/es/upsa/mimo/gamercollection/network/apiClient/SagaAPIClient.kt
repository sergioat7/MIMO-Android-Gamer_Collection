package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.network.apiService.SagaAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class SagaAPIClient {

    //region Private properties
    private val api = ApiManager.getService<SagaAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.getSagas(headers)

        ApiManager.sendServer(request, success, failure)
    }

    fun createSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.createSaga(headers, saga)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.setSaga(headers, saga.id, saga)

        ApiManager.sendServer(request, success, failure)
    }

    fun deleteSaga(sagaId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.deleteSaga(headers, sagaId)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}