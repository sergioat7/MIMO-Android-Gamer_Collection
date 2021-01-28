package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SagaRepository @Inject constructor(
    private val database: AppDatabase,
    private val sagaAPIClient: SagaAPIClient
) {

    // MARK: - Public methods

    fun loadSagas(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        sagaAPIClient.getSagas({ newSagas ->

            for (newSaga in newSagas) {
                insertSagaDatabase(newSaga)
            }
            val currentSagas = getSagasDatabase()
            val sagasToRemove = AppDatabase.getDisabledContent(currentSagas, newSagas)
            for (saga in sagasToRemove) {
                deleteSagaDatabase(saga as SagaResponse)
            }
            success()
        }, failure)
    }

    fun createSaga(saga: SagaResponse, success: (SagaResponse?) -> Unit, failure: (ErrorResponse) -> Unit) {

        sagaAPIClient.createSaga(saga, {
            loadSagas({

                val sagas = getSagasDatabase()
                val newSagaCreated = sagas.firstOrNull { s ->
                    val game = s.games.firstOrNull { game ->
                        game.id == saga.games.firstOrNull()?.id
                    }
                    game != null
                }

                success(newSagaCreated)
            }, failure)
        }, failure)
    }

    fun setSaga(saga: SagaResponse, success: (SagaResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        sagaAPIClient.setSaga(saga, {

            updateSagaDatabase(it)
            success(it)
        }, failure)
    }

    fun deleteSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        sagaAPIClient.deleteSaga(saga.id, {

            deleteSagaDatabase(saga)
            success()
        }, failure)
    }

    fun getSagasDatabase(): List<SagaResponse> {

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

    fun getSagaDatabase(sagaId: Int): SagaResponse? {

        var saga: SagaWithGames? = null
        runBlocking {
            val result = GlobalScope.async { database.sagaDao().getSaga(sagaId) }
            saga = result.await()
        }
        return saga?.transform()
    }

    fun resetTable() {

        val sagas = getSagasDatabase()
        for (saga in sagas) {
            deleteSagaDatabase(saga)
        }
    }

    // MARK: - Private methods

    private fun insertSagaDatabase(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().insertSaga(saga)
        }
    }

    private fun updateSagaDatabase(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().updateSaga(saga)
        }
    }

    private fun deleteSagaDatabase(saga: SagaResponse) {

        GlobalScope.launch {
            database.sagaDao().deleteSaga(saga)
        }
    }
}