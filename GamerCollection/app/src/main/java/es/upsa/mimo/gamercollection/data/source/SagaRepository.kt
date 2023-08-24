package es.upsa.mimo.gamercollection.data.source

import es.upsa.mimo.gamercollection.data.source.di.IoDispatcher
import es.upsa.mimo.gamercollection.data.source.di.MainDispatcher
import es.upsa.mimo.gamercollection.database.daos.SagaDao
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.network.interfaces.SagaApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SagaRepository @Inject constructor(
    private val api: SagaApiService,
    private val sagaDao: SagaDao,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadSagas(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.getSagas())) {
//                    is RequestResult.JsonSuccess -> {
//
//                        val newSagas = response.body
//                        for (newSaga in newSagas) {
//                            insertSagaDatabase(newSaga)
//                        }
//                        val currentSagas = getSagasDatabase()
//                        val sagasToRemove = AppDatabase.getDisabledContent(currentSagas, newSagas)
//                        for (saga in sagasToRemove) {
//                            deleteSagaDatabase(saga as SagaResponse)
//                        }
//                        success()
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun createSaga(
        newSaga: SagaResponse,
        success: (SagaResponse?) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        newSaga.id = getNextId()
        insertSagaDatabase(newSaga)
        success(newSaga)
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.createSaga(newSaga))) {
//                    is RequestResult.Success -> {
//                        loadSagas({
//
//                            val currentSagas = getSagasDatabase()
//                            val newSagaCreated = currentSagas.firstOrNull { saga ->
//                                val game = saga.games.firstOrNull { game ->
//                                    game.id == newSaga.games.firstOrNull()?.id
//                                }
//                                game != null
//                            }
//
//                            success(newSagaCreated)
//                        }, failure)
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun setSaga(
        saga: SagaResponse,
        success: (SagaResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        updateSagaDatabase(saga)
        success(saga)
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.setSaga(saga.id, saga))) {
//                    is RequestResult.JsonSuccess -> {
//                        updateSagaDatabase(response.body)
//                        success(response.body)
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun deleteSaga(saga: SagaResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        deleteSagaDatabase(saga)
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.deleteSaga(saga.id))) {
//                    is RequestResult.Success -> {
//                        deleteSagaDatabase(saga)
//                        success()
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    fun getSagasDatabase(): List<SagaResponse> {

        var sagas: List<SagaWithGames> = arrayListOf()
        runBlocking {

            val result = databaseScope.async {
                sagaDao.getSagas()
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
                sagaDao.getSaga(sagaId)
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
                sagaDao.insertSaga(saga)
            }
            job.join()
        }
    }

    private fun updateSagaDatabase(saga: SagaResponse) {

        runBlocking {
            val job = databaseScope.launch {
                sagaDao.updateSaga(saga)
            }
            job.join()
        }
    }

    private fun deleteSagaDatabase(saga: SagaResponse) {

        runBlocking {
            val job = databaseScope.launch {
                sagaDao.deleteSaga(saga)
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