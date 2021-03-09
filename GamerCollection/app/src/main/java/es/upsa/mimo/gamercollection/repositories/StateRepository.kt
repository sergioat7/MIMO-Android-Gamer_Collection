package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.network.apiClient.StateAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class StateRepository @Inject constructor(
    private val database: AppDatabase,
    private val stateAPIClient: StateAPIClient
) {

    // MARK: - Private properties

    private val databaseScope = CoroutineScope(Job() + Dispatchers.IO)

    // MARK: - Public methods

    fun loadStates(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        stateAPIClient.getStates({ newStates ->

            for (newState in newStates) {
                insertStateDatabase(newState)
            }
            val currentStates = getStatesDatabase()
            val statesToRemove = AppDatabase.getDisabledContent(currentStates, newStates)
            for (state in statesToRemove) {
                deleteStateDatabase(state as StateResponse)
            }
            success()
        }, failure)
    }

    fun getStatesDatabase(): List<StateResponse> {

        var states = mutableListOf<StateResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.stateDao().getStates()
            }
            states = result.await().toMutableList()
            states.sortBy { it.name }
            val other = states.firstOrNull { it.id == Constants.OTHER_VALUE }
            states.remove(other)
            other?.let {
                states.add(it)
            }
        }
        return states
    }

    fun resetTable() {

        val states = getStatesDatabase()
        for (state in states) {
            deleteStateDatabase(state)
        }
    }

    // MARK: - Private methods

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
}