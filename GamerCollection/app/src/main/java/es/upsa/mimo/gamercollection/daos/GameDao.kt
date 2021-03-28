package es.upsa.mimo.gamercollection.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import es.upsa.mimo.gamercollection.models.GameWithSaga
import es.upsa.mimo.gamercollection.models.responses.GameResponse

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