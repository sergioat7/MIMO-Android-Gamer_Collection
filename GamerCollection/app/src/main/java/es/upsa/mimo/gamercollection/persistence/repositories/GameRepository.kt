package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val gameDao = database.gameDao()

    fun getGames(): List<GameResponse> {

        var games: List<GameResponse> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { gameDao.getGames() }
            games = result.await()
        }
        return games
    }

    fun insertGame(game: GameResponse) {

        GlobalScope.launch {
            gameDao.insertGame(game)
        }
    }

    fun deleteGame(game: GameResponse) {

        GlobalScope.launch {
            gameDao.deleteGame(game)
        }
    }

    fun removeDisableContent(newGames: List<GameResponse>) {

        val currentGames = getGames()
        val games = AppDatabase.getDisabledContent(currentGames, newGames)
        for (game in games) {
            deleteGame(game)
        }
    }
}