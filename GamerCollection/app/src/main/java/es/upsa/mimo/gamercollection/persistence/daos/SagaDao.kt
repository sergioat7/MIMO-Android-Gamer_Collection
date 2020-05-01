package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.SagaResponse

@Dao
interface SagaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSaga(saga: SagaResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSaga(saga: SagaResponse)

    @Delete
    fun deleteSaga(saga: SagaResponse)

    @Query("SELECT * FROM Saga WHERE id == :sagaId")
    fun getSaga(sagaId: String): SagaResponse

    @Query("SELECT * FROM Saga")
    fun getSagas(): List<SagaResponse>
}