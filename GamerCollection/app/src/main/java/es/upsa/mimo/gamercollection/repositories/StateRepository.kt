package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.network.apiClient.StateAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class StateRepository @Inject constructor(
    private val database: AppDatabase,
    private val stateAPIClient: StateAPIClient
) {

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
            val result = GlobalScope.async { database.stateDao().getStates() }
            states = result.await().toMutableList()
            states.sortBy { it.name }
            val other = states.firstOrNull { it.id == Constants.DEFAULT_PLATFORM }
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

        GlobalScope.launch {
            database.stateDao().insertState(state)
        }
    }

    private fun deleteStateDatabase(state: StateResponse) {

        GlobalScope.launch {
            database.stateDao().deleteState(state)
        }
    }
}