package es.upsa.mimo.gamercollection.database.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.SagaWithGames
import es.upsa.mimo.gamercollection.models.SagaResponse

@Dao
interface SagaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaga(saga: SagaResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSaga(saga: SagaResponse)

    @Delete
    suspend fun deleteSaga(saga: SagaResponse)

    @Query("SELECT * FROM Saga WHERE id == :sagaId")
    suspend fun getSaga(sagaId: Int): SagaWithGames

    @Query("SELECT * FROM Saga")
    suspend fun getSagas(): List<SagaWithGames>
}