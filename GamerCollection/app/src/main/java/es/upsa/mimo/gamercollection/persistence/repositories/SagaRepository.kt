package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SagaRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val sagaDao = database.sagaDao()

    fun getSagas(): List<SagaResponse> {

        var sagas: List<SagaResponse> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { sagaDao.getSagas() }
            sagas = result.await()
        }
        return sagas
    }

    fun insertSaga(saga: SagaResponse) {

        GlobalScope.launch {
            sagaDao.insertSaga(saga)
        }
    }

    fun deleteSaga(saga: SagaResponse) {

        GlobalScope.launch {
            sagaDao.deleteSaga(saga)
        }
    }

    fun removeDisableContent(newSagas: List<SagaResponse>) {

        val currentSagas = getSagas()
        val sagas = AppDatabase.getDisabledContent(currentSagas, newSagas)
        for (saga in sagas) {
            deleteSaga(saga)
        }
    }
}