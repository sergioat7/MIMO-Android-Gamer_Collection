package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SagaResponse

interface SagaRepository {
    fun loadSagas(success: () -> Unit, failure: (ErrorResponse) -> Unit)
    fun createSaga(
        newSaga: SagaResponse,
        success: (SagaResponse?) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun deleteSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit)
    fun getSagasDatabase(): List<SagaResponse>
    fun getSagaDatabase(sagaId: Int): SagaResponse?
    fun insertSagaDatabase(saga: SagaResponse)
    fun resetTable()
}