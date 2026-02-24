package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Saga

interface SagaRepository {
    fun loadSagas(success: () -> Unit, failure: (ErrorModel) -> Unit)
    fun createSaga(
        newSaga: Saga,
        success: (Saga?) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun setSaga(
        saga: Saga,
        success: (Saga) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun deleteSaga(saga: Saga, success: () -> Unit, failure: (ErrorModel) -> Unit)
    fun getSagasDatabase(): List<Saga>
    fun getSagaDatabase(sagaId: Int): Saga?
    fun insertSagaDatabase(saga: Saga)
    fun resetTable()
}