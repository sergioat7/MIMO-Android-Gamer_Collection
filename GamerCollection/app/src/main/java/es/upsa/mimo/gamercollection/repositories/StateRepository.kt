package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class StateRepository @Inject constructor(
    private val database: AppDatabase
) {

    // MARK: - Public methods

    fun getStates(): List<StateResponse> {

        var states = mutableListOf<StateResponse>()
        runBlocking {
            val result = GlobalScope.async { database.stateDao().getStates() }
            states = result.await().toMutableList()
            states.sortBy { it.name }
            val other = states.firstOrNull { it.id == "OTHER" }
            states.remove(other)
            other?.let {
                states.add(it)
            }
        }
        return states
    }

    fun manageStates(newStates: List<StateResponse>) {

        for (newState in newStates) {
            insertState(newState)
        }

        val currentStates = getStates()
        val statesToRemove = AppDatabase.getDisabledContent(currentStates, newStates)
        for (state in statesToRemove) {
            deleteState(state as StateResponse)
        }
    }

    // MARK: - Private methods

    private fun insertState(state: StateResponse) {

        GlobalScope.launch {
            database.stateDao().insertState(state)
        }
    }

    private fun deleteState(state: StateResponse) {

        GlobalScope.launch {
            database.stateDao().deleteState(state)
        }
    }
}