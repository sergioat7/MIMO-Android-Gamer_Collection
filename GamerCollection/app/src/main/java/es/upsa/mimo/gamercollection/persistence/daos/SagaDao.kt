package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SagaWithGames

@Dao
interface SagaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaga(saga: SagaResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSaga(saga: SagaResponse)

    @Delete
    suspend fun deleteSaga(saga: SagaResponse)

    @Query("SELECT * FROM Saga WHERE id == :sagaId")
    suspend fun getSaga(sagaId: String): SagaWithGames

    @Query("SELECT * FROM Saga")
    suspend fun getSagas(): List<SagaWithGames>
}