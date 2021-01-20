package es.upsa.mimo.gamercollection.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class StateRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val stateDao = database.stateDao()

    fun getStates(): List<StateResponse> {

        var states = mutableListOf<StateResponse>()
        runBlocking {
            val result = GlobalScope.async { stateDao.getStates() }
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

    fun insertState(state: StateResponse) {

        GlobalScope.launch {
            stateDao.insertState(state)
        }
    }

    private fun deleteState(state: StateResponse) {

        GlobalScope.launch {
            stateDao.deleteState(state)
        }
    }

    fun removeDisableContent(newStates: List<StateResponse>) {

        val currentStates = getStates()
        val states = AppDatabase.getDisabledContent(currentStates, newStates) as List<*>
        for (state in states) {
            deleteState(state as StateResponse)
        }
    }
}