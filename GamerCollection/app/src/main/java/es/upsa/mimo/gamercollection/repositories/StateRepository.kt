package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.injection.modules.IoDispatcher
import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.network.StateApiService
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class StateRepository @Inject constructor(
    private val api: StateApiService,
    private val database: AppDatabase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadStates(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            try {
                when (val response = ApiManager.validateResponse(api.getStates())) {
                    is RequestResult.JsonSuccess -> {

                        val newStates = response.body
                        for (newState in newStates) {
                            insertStateDatabase(newState)
                        }
                        val currentStates = getStatesDatabase()
                        val statesToRemove =
                            AppDatabase.getDisabledContent(currentStates, newStates)
                        for (state in statesToRemove) {
                            deleteStateDatabase(state as StateResponse)
                        }
                        success()
                    }
                    is RequestResult.Failure -> failure(response.error)
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
            }
        }
    }

    fun resetTable() {

        val states = getStatesDatabase()
        for (state in states) {
            deleteStateDatabase(state)
        }
    }
    //endregion


    //region Private methods
    private fun getStatesDatabase(): List<StateResponse> {

        var states = mutableListOf<StateResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.stateDao().getStates()
            }
            states = result.await().toMutableList()
            states.sortBy { it.name }
            val other = states.firstOrNull { it.id == ApiManager.OTHER_VALUE }
            states.remove(other)
            other?.let {
                states.add(it)
            }
        }
        return states
    }

    private fun insertStateDatabase(state: StateResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.stateDao().insertState(state)
            }
            job.join()
        }
    }

    private fun deleteStateDatabase(state: StateResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.stateDao().deleteState(state)
            }
            job.join()
        }
    }
    //endregion
}