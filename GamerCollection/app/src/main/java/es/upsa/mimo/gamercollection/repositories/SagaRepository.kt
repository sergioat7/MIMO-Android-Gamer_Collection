package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SagaRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getSagas(): List<SagaResponse> {

        var sagas: List<SagaWithGames> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { database.sagaDao().getSagas() }
            sagas = result.await()
        }
        val result = ArrayList<SagaResponse>()
        for (saga in sagas) {
            result.add(saga.transform())
        }
        return result
    }

    fun getSaga(sagaId: Int): SagaResponse? {

        var saga: SagaWithGames? = null
        runBlocking {
            val result = GlobalScope.async { database.sagaDao().getSaga(sagaId) }
            saga = result.await()
        }
        return saga?.transform()
    }

    fun insertSaga(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().insertSaga(saga)
        }
    }

    fun updateSaga(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().updateSaga(saga)
        }
    }

    fun deleteSaga(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().deleteSaga(saga)
        }
    }

    fun removeDisableContent(newSagas: List<SagaResponse>) {

        val currentSagas = getSagas()
        val sagas = AppDatabase.getDisabledContent(currentSagas, newSagas) as List<*>
        for (saga in sagas) {
            deleteSaga(saga as SagaResponse)
        }
    }
}