package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.StateResponse

@Dao
interface StateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertState(state: StateResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateState(state: StateResponse)

    @Delete
    fun deleteState(state: StateResponse)

    @Query("SELECT * FROM State WHERE id == :stateId")
    fun getState(stateId: String): StateResponse

    @Query("SELECT * FROM State")
    fun getStates(): List<StateResponse>
}