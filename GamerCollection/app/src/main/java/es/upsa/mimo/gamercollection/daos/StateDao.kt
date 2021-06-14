package es.upsa.mimo.gamercollection.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.responses.StateResponse

@Dao
interface StateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: StateResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateState(state: StateResponse)

    @Delete
    suspend fun deleteState(state: StateResponse)

    @Query("SELECT * FROM State WHERE id == :stateId")
    suspend fun getState(stateId: String): StateResponse

    @Query("SELECT * FROM State")
    suspend fun getStates(): List<StateResponse>
}