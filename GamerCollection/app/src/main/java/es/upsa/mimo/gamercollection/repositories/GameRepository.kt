package es.upsa.mimo.gamercollection.repositories

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.GameWithSaga
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val database: AppDatabase,
    private val gameAPIClient: GameAPIClient
) {

    // MARK: - Public methods

    fun loadGames(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        gameAPIClient.getGames({ newGames ->

            for (newGame in newGames) {
                insertGameDatabase(newGame)
            }
            val currentGames = getGamesDatabase()
            val gamesToRemove = AppDatabase.getDisabledContent(currentGames, newGames)
            for (game in gamesToRemove) {
                deleteGameDatabase(game as GameResponse)
            }
            success()
        }, failure)
    }

    fun createGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        gameAPIClient.createGame(game, {
            loadGames(success, failure)
        }, failure)
    }

    fun setGame(game: GameResponse, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        gameAPIClient.setGame(game, {

            updateGameDatabase(it)
            success(it)
        }, failure)
    }

    fun deleteGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        gameAPIClient.deleteGame(game.id, {

            deleteGameDatabase(game)
            success()
        }, failure)
    }

    fun getGamesDatabase(query: SupportSQLiteQuery? = null): List<GameResponse> {

        var games: List<GameWithSaga> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async {
                database
                    .gameDao()
                    .getGames(query ?: SimpleSQLiteQuery("SELECT * FROM Game"))
            }
            games = result.await()
        }
        val result = ArrayList<GameResponse>()
        for (game in games) {
            result.add(game.transform())
        }
        return result
    }

    fun getGameDatabase(gameId: Int): GameResponse? {

        var game: GameWithSaga? = null
        runBlocking {
            val result = GlobalScope.async {
                database
                    .gameDao()
                    .getGames(SimpleSQLiteQuery("SELECT * FROM Game WHERE id == '${gameId}'"))
                    .firstOrNull()
            }
            game = result.await()
        }
        return game?.transform()
    }

    fun removeSagaFromGames(saga: SagaResponse) {

        val newSagaGames = saga.games
        val allGames = getGamesDatabase()
        val oldSagaGames = allGames.filter { it.saga?.id  == saga.id }

        for (oldSagaGame in oldSagaGames) {
            if (newSagaGames.firstOrNull { it.id == oldSagaGame.id } == null) {

                oldSagaGame.saga = null
                updateGameDatabase(oldSagaGame)
            }
        }

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newSagaGame in newSagaGames) {

            newSagaGame.saga = sagaVar
            updateGameDatabase(newSagaGame)
        }
    }

    fun updateSagaGames(saga: SagaResponse) {

        val sagaVar = SagaResponse(saga.id, saga.name, arrayListOf())
        for (newGame in saga.games) {

            newGame.saga = sagaVar
            updateGameDatabase(newGame)
        }
    }

    fun updateGameSongs(gameId: Int, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        gameAPIClient.getGame(gameId, {

            updateGameDatabase(it)
            success(it)
        }, failure)
    }

    fun resetTable() {

        val games = getGamesDatabase()
        for (game in games) {
            deleteGameDatabase(game)
        }
    }

    // MARK: - Private methods

    private fun insertGameDatabase(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().insertGame(game)
        }
    }

    private fun updateGameDatabase(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().updateGame(game)
        }
    }

    private fun deleteGameDatabase(game: GameResponse) {

        GlobalScope.launch {
            database.gameDao().deleteGame(game)
        }
    }
}