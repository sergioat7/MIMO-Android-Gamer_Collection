package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SagaRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val sagaDao = database.sagaDao()

    fun getSagas(): List<SagaResponse> {

        var sagas: List<SagaWithGames> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { sagaDao.getSagas() }
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
            val result = GlobalScope.async { sagaDao.getSaga(sagaId) }
            saga = result.await()
        }
        return saga?.transform()
    }

    fun insertSaga(saga: SagaResponse) {

        GlobalScope.launch {
            sagaDao.insertSaga(saga)
        }
    }

    fun updateSaga(saga: SagaResponse) {

        GlobalScope.launch {
            sagaDao.updateSaga(saga)
        }
    }

    fun deleteSaga(saga: SagaResponse) {

        GlobalScope.launch {
            sagaDao.deleteSaga(saga)
        }
    }

    fun removeDisableContent(newSagas: List<SagaResponse>) {

        val currentSagas = getSagas()
        val sagas = AppDatabase.getDisabledContent(currentSagas, newSagas) as List<SagaResponse>
        for (saga in sagas) {
            deleteSaga(saga)
        }
    }
}