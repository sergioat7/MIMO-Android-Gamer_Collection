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

    fun deleteGame(game: GameResponse) {
        gameDao.deleteGame(game)
    }

    fun removeDisableContent(newGames: List<GameResponse>) {

        val currentGames = getGames()
        val games = AppDatabase.getDisabledContent(currentGames, newGames)
        for (game in games) {
            deleteGame(game)
        }
    }
}