package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.StateResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class StateRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val stateDao = database.stateDao()

    fun getStates(): List<StateResponse> {
        return stateDao.getStates()
    }

    fun insertState(state: StateResponse) {
        stateDao.insertState(state)
    }
}