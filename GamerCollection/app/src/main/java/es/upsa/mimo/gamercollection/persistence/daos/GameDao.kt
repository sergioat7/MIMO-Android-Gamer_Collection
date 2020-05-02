package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.GameResponse

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGame(game: GameResponse)

    @Delete
    suspend fun deleteGame(game: GameResponse)

    @Query("SELECT * FROM Game WHERE id == :gameId")
    suspend fun getGame(gameId: String): GameResponse

    @Query("SELECT * FROM Game")
    suspend fun getGames(): List<GameResponse>
}