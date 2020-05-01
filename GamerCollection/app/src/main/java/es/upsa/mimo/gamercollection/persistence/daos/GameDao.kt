package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.GameResponse

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(game: GameResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateGame(game: GameResponse)

    @Delete
    fun deleteGame(game: GameResponse)

    @Query("SELECT * FROM Game WHERE id == :gameId")
    fun getGame(gameId: String): GameResponse

    @Query("SELECT * FROM Game")
    fun getGames(): List<GameResponse>
}