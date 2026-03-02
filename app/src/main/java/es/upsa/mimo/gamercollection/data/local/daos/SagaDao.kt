package es.upsa.mimo.gamercollection.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import es.upsa.mimo.gamercollection.data.local.model.SagaEntity
import es.upsa.mimo.gamercollection.data.local.model.SagaWithGames

@Dao
interface SagaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaga(saga: SagaEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSaga(saga: SagaEntity)

    @Delete
    suspend fun deleteSaga(saga: SagaEntity)

    @Query("SELECT * FROM Saga WHERE id == :sagaId")
    suspend fun getSaga(sagaId: Int): SagaWithGames?

    @Query("SELECT * FROM Saga")
    suspend fun getSagas(): List<SagaWithGames>
}