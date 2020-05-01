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
}