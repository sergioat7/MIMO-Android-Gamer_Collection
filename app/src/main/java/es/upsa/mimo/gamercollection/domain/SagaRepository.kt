package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.Saga

interface SagaRepository {
    suspend fun createSaga(newSaga: Saga): Saga
    suspend fun setSaga(saga: Saga)
    suspend fun deleteSaga(saga: Saga)
    suspend fun getSagasDatabase(): List<Saga>
    suspend fun getSagaDatabase(sagaId: Int): Saga?
    suspend fun insertSagaDatabase(saga: Saga)
    suspend fun resetTable()
}