package es.upsa.mimo.gamercollection.repositories

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.GameWithSaga
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getGames(query: SupportSQLiteQuery? = null): List<GameResponse> {

        var games: List<GameWithSaga> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { database.gameDao().getGames(query ?: SimpleSQLiteQuery("SELECT * FROM Game")) }
            games = result.await()
        }
        val result = ArrayList<GameResponse>()
        for (game in games) {
            result.add(game.transform())
        }
        return result
    }

    fun getGame(gameId: Int): GameResponse? {

        var game: GameWithSaga? = null
        runBlocking {
            val result = GlobalScope.async { database.gameDao().getGames(SimpleSQLiteQuery("SELECT * FROM Game WHERE id == '${gameId}'")).firstOrNull() }
            game = result.await()
        }
        return game?.transform()
    }

    fun insertGame(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().insertGame(game)
        }
    }

    fun updateGame(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().updateGame(game)
        }
    }

    fun deleteGame(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().deleteGame(game)
        }
    }

    fun removeDisableContent(newGames: List<GameResponse>) {

        val currentGames = getGames()
        val games = AppDatabase.getDisabledContent(currentGames, newGames) as List<*>
        for (game in games) {
            deleteGame(game as GameResponse)
        }
    }

    fun manageGames(games: List<GameResponse>) {

        for (game in games) {
            insertGame(game)
        }
        removeDisableContent(games)
    }
}