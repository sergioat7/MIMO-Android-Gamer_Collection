package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class GameRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val gameDao = database.gameDao()

    fun getGames(): List<GameResponse> {
        return gameDao.getGames()
    }

    fun insertGame(game: GameResponse) {
        gameDao.insertGame(game)
    }
}