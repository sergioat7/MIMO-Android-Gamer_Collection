package es.upsa.mimo.gamercollection.data

import es.upsa.mimo.gamercollection.data.di.IoDispatcher
import es.upsa.mimo.gamercollection.data.local.daos.SagaDao
import es.upsa.mimo.gamercollection.data.local.model.SagaWithGames
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.domain.toLocalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SagaRepositoryImpl @Inject constructor(
    private val sagaDao: SagaDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SagaRepository {

    //region Private properties
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    override fun createSaga(
        newSaga: Saga,
        success: (Saga?) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        val saga = newSaga.copy(id = getNextId())
        insertSagaDatabase(saga)
        success(saga)
    }

    override fun setSaga(
        saga: Saga,
        success: (Saga) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        updateSagaDatabase(saga)
        success(saga)
    }

    override fun deleteSaga(saga: Saga, success: () -> Unit, failure: (ErrorModel) -> Unit) {
        deleteSagaDatabase(saga)
        success()
    }

    override fun getSagasDatabase(): List<Saga> {

        var sagas: List<SagaWithGames> = arrayListOf()
        runBlocking {

            val result = databaseScope.async {
                sagaDao.getSagas()
            }
            sagas = result.await()
        }
        val result = ArrayList<Saga>()
        for (saga in sagas) {
            result.add(saga.transform().toDomain())
        }
        return result
    }

    override fun getSagaDatabase(sagaId: Int): Saga? {

        var saga: SagaWithGames? = null
        runBlocking {

            val result = databaseScope.async {
                sagaDao.getSaga(sagaId)
            }
            saga = result.await()
        }
        return saga?.transform()?.toDomain()
    }

    override fun insertSagaDatabase(saga: Saga) {

        runBlocking {
            val job = databaseScope.launch {
                sagaDao.insertSaga(saga.toLocalData())
            }
            job.join()
        }
    }

    override fun resetTable() {

        val sagas = getSagasDatabase()
        for (saga in sagas) {
            deleteSagaDatabase(saga)
        }
    }
    //endregion

    //region Private methods
    private fun updateSagaDatabase(saga: Saga) {

        runBlocking {
            val job = databaseScope.launch {
                sagaDao.updateSaga(saga.toLocalData())
            }
            job.join()
        }
    }

    private fun deleteSagaDatabase(saga: Saga) {

        runBlocking {
            val job = databaseScope.launch {
                sagaDao.deleteSaga(saga.toLocalData())
            }
            job.join()
        }
    }

    private fun getNextId(): Int {

        val sagas = getSagasDatabase()
        return if (sagas.isNotEmpty()) {
            sagas.maxOf { it.id } + 1
        } else {
            0
        }
    }
    //endregion
}