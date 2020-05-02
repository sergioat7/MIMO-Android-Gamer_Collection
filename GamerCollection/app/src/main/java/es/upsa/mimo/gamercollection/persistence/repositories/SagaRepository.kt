package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class SagaRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val sagaDao = database.sagaDao()

    fun getSagas(): List<SagaResponse> {
        return sagaDao.getSagas()
    }

    fun insertSaga(saga: SagaResponse) {
        sagaDao.insertSaga(saga)
    }

    fun deleteSaga(saga: SagaResponse) {
        sagaDao.deleteSaga(saga)
    }

    fun removeDisableContent(newSagas: List<SagaResponse>) {

        val currentSagas = getSagas()
        val sagas = AppDatabase.getDisabledContent(currentSagas, newSagas)
        for (saga in sagas) {
            deleteSaga(saga)
        }
    }
}