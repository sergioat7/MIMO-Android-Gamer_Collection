package es.upsa.mimo.gamercollection.data.local.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import es.upsa.mimo.gamercollection.data.local.model.GameEntity
import es.upsa.mimo.gamercollection.data.local.model.GameWithSaga

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @RawQuery
    suspend fun getGames(query: SupportSQLiteQuery): List<GameWithSaga>
}