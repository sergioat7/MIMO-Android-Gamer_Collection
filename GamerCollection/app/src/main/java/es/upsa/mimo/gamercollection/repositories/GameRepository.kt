package es.upsa.mimo.gamercollection.repositories

import androidx.sqlite.db.SimpleSQLiteQuery
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
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

    fun getGamesDatabase(state: String? = null,
                         filters: FilterModel? = null,
                         sortKey: String? = null,
                         ascending: Boolean = true): List<GameResponse> {

        var queryString = "SELECT * FROM Game"

        var queryConditions = when(state) {
            Constants.PENDING_STATE -> " WHERE state == '${Constants.PENDING_STATE}' AND "
            Constants.IN_PROGRESS_STATE -> " WHERE state == '${Constants.IN_PROGRESS_STATE}' AND "
            Constants.FINISHED_STATE -> " WHERE state == '${Constants.FINISHED_STATE}' AND "
            else -> ""
        }

        filters?.let { filtersVar ->

            if (queryConditions.isEmpty()) queryConditions += " WHERE "

            var queryPlatforms = ""
            val platforms = filtersVar.platforms
            if (platforms.isNotEmpty()) {
                queryPlatforms += "("
                for (platform in platforms) {
                    queryPlatforms += "platform == '${platform}' OR "
                }
                queryPlatforms = queryPlatforms.dropLast(4) + ") AND "
            }

            var queryGenres = ""
            val genres = filtersVar.genres
            if (genres.isNotEmpty()){
                queryGenres += "("
                for (genre in genres) {
                    queryGenres += "genre == '${genre}' OR "
                }
                queryGenres = queryGenres.dropLast(4) + ") AND "
            }

            var queryFormats = ""
            val formats = filtersVar.formats
            if (formats.isNotEmpty()){
                queryFormats += "("
                for (format in formats) {
                    queryFormats += "format == '${format}' OR "
                }

                queryFormats = queryFormats.dropLast(4) + ") AND "
            }

            queryConditions += queryPlatforms + queryGenres + queryFormats

            queryConditions += "score >= ${filtersVar.minScore} AND score <= ${filtersVar.maxScore} AND "

            if (filtersVar.minReleaseDate != null) {
                queryConditions += "releaseDate >= '${filtersVar.minReleaseDate.time}' AND "
            }
            if (filtersVar.maxReleaseDate != null) {
                queryConditions += "releaseDate <= '${filtersVar.maxReleaseDate.time}' AND "
            }

            if (filtersVar.minPurchaseDate != null) {
                queryConditions += "purchaseDate >= '${filtersVar.minPurchaseDate.time}' AND "
            }
            if (filtersVar.maxPurchaseDate != null) {
                queryConditions += "purchaseDate <= '${filtersVar.maxPurchaseDate.time}' AND "
            }

            queryConditions += "price >= ${filtersVar.minPrice} AND "
            if (filtersVar.maxPrice > 0) {
                queryConditions += "price <= ${filtersVar.maxPrice} AND "
            }

            if (filtersVar.isGoty) {
                queryConditions += "goty == 1 AND "
            }

            if (filtersVar.isLoaned) {
                queryConditions += "loanedTo != null AND "
            }

            if (filtersVar.hasSaga) {
                queryConditions += "saga_id != -1 AND "
            }

            if (filtersVar.hasSongs) {
                queryConditions += "songs != '[]' AND "
            }
        }
        queryConditions = queryConditions.dropLast(5)
        queryString += queryConditions

        queryString += " ORDER BY "
        sortKey.let {
            val order = if(ascending) "ASC"  else "DESC"
            queryString += "$sortKey $order, "
        }
        queryString += "name ASC"

        val query = SimpleSQLiteQuery(queryString)

        var games: List<GameWithSaga> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async {
                database
                    .gameDao()
                    .getGames(query)
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