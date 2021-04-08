package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.SagaApiService

class SagaAPIClient {

    //region Private properties
    private val api = ApiManager.getService<SagaApiService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun getSagas(success: (List<SagaResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.getSagas()
        ApiManager.sendServer(request, success, failure)
    }

    fun createSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.createSaga(saga)
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val request = api.setSaga(saga.id, saga)
        ApiManager.sendServer(request, success, failure)
    }

    fun deleteSaga(sagaId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val request = api.deleteSaga(sagaId)
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}