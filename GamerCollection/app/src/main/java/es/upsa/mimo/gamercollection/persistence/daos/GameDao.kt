package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.GameWithSaga

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGame(game: GameResponse)

    @Delete
    suspend fun deleteGame(game: GameResponse)

    @RawQuery
    suspend fun getGames(query: SupportSQLiteQuery): List<GameWithSaga>
}