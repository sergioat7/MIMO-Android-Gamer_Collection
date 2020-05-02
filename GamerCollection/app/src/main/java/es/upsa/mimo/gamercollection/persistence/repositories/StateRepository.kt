package es.upsa.mimo.gamercollection.persistence.repositories

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

        var states: List<StateResponse> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { stateDao.getStates() }
            states = result.await()
        }
        return states
    }

    fun insertState(state: StateResponse) {

        GlobalScope.launch {
            stateDao.insertState(state)
        }
    }

    fun deleteState(state: StateResponse) {

        GlobalScope.launch {
            stateDao.deleteState(state)
        }
    }

    fun removeDisableContent(newStates: List<StateResponse>) {

        val currentStates = getStates()
        val states = AppDatabase.getDisabledContent(currentStates, newStates)
        for (state in states) {
            deleteState(state)
        }
    }
}