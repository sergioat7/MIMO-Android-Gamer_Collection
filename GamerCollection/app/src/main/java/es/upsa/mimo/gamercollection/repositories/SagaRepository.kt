package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.injection.modules.IoDispatcher
import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.network.SagaApiService
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.*
import javax.inject.Inject

class SagaRepository @Inject constructor(
    private val api: SagaApiService,
    private val database: AppDatabase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadSagas(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.getSagas())) {
                is RequestResult.JsonSuccess -> {

                    val newSagas = response.body
                    for (newSaga in newSagas) {
                        insertSagaDatabase(newSaga)
                    }
                    val currentSagas = getSagasDatabase()
                    val sagasToRemove = AppDatabase.getDisabledContent(currentSagas, newSagas)
                    for (saga in sagasToRemove) {
                        deleteSagaDatabase(saga as SagaResponse)
                    }
                    success()
                }
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun createSaga(
        saga: SagaResponse,
        success: (SagaResponse?) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.createSaga(saga))) {
                is RequestResult.Success -> {
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
                }
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.setSaga(saga.id, saga))) {
                is RequestResult.JsonSuccess -> {
                    updateSagaDatabase(response.body)
                    success(response.body)
                }
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun deleteSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.deleteSaga(saga.id))) {
                is RequestResult.Success -> {
                    deleteSagaDatabase(saga)
                    success()
                }
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun getSagasDatabase(): List<SagaResponse> {

        var sagas: List<SagaWithGames> = arrayListOf()
        runBlocking {

            val result = databaseScope.async {
                database.sagaDao().getSagas()
            }
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

            val result = databaseScope.async {
                database.sagaDao().getSaga(sagaId)
            }
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
    //endregion

    //region Private methods
    private fun insertSagaDatabase(saga: SagaResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.sagaDao().insertSaga(saga)
            }
            job.join()
        }
    }

    private fun updateSagaDatabase(saga: SagaResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.sagaDao().updateSaga(saga)
            }
            job.join()
        }
    }

    private fun deleteSagaDatabase(saga: SagaResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.sagaDao().deleteSaga(saga)
            }
            job.join()
        }
    }
    //endregion
}