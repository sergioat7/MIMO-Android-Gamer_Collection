package es.upsa.mimo.gamercollection.data

import es.upsa.mimo.gamercollection.data.local.daos.SagaDao
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.domain.toLocalData
import javax.inject.Inject

class SagaRepositoryImpl @Inject constructor(
    private val sagaDao: SagaDao,
) : SagaRepository {

    //region Public methods
    override suspend fun createSaga(newSaga: Saga): Saga {
        return newSaga.copy(id = getNextId()).also {
            sagaDao.insertSaga(it.toLocalData())
        }
    }

    override suspend fun setSaga(saga: Saga) {
        sagaDao.updateSaga(saga.toLocalData())
    }

    override suspend fun deleteSaga(saga: Saga) {
        sagaDao.deleteSaga(saga.toLocalData())
    }

    override suspend fun getSagasDatabase(): List<Saga> {
        return sagaDao.getSagas().map { it.transform().toDomain() }
    }

    override suspend fun getSagaDatabase(sagaId: Int): Saga? {
        return sagaDao.getSaga(sagaId)?.transform()?.toDomain()
    }

    override suspend fun insertSagaDatabase(saga: Saga) {
        sagaDao.insertSaga(saga.toLocalData())
    }

    override suspend fun resetTable() {

        val sagas = sagaDao.getSagas().map { it.transform() }
        for (saga in sagas) {
            sagaDao.deleteSaga(saga)
        }
    }
    //endregion

    //region Private methods
    private suspend fun getNextId(): Int {

        val sagas = sagaDao.getSagas()
        return if (sagas.isNotEmpty()) {
            sagas.maxOf { it.saga.id } + 1
        } else {
            0
        }
    }
    //endregion
}