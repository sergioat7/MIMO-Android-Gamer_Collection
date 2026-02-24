package es.upsa.mimo.gamercollection.data

import androidx.sqlite.db.SimpleSQLiteQuery
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.di.IoDispatcher
import es.upsa.mimo.gamercollection.data.di.MainDispatcher
import es.upsa.mimo.gamercollection.data.local.daos.GameDao
import es.upsa.mimo.gamercollection.data.remote.model.ErrorResponse
import es.upsa.mimo.gamercollection.domain.model.FilterModel
import es.upsa.mimo.gamercollection.data.local.model.GameWithSaga
import es.upsa.mimo.gamercollection.data.remote.model.RawgGameResponse
import es.upsa.mimo.gamercollection.data.remote.ApiManager
import es.upsa.mimo.gamercollection.data.remote.RequestResult
import es.upsa.mimo.gamercollection.data.remote.interfaces.GameApiService
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import es.upsa.mimo.gamercollection.data.remote.model.GameResponse
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.domain.toLocalData
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.HashMap
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val api: GameApiService,
    private val apiRawg: RawgGameApiService,
    private val gameDao: GameDao,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GameRepository {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    override fun loadGames(success: () -> Unit, failure: (ErrorModel) -> Unit) {
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.getGames())) {
//                    is RequestResult.JsonSuccess -> {
//
//                        val newGames = response.body
//                        for (newGame in newGames) {
//                            insertGameDatabase(newGame)
//                        }
//                        val currentGames = getGamesDatabase()
//                        val gamesToRemove = AppDatabase.getDisabledContent(currentGames, newGames)
//                        for (game in gamesToRemove) {
//                            deleteGameDatabase(game as GameResponse)
//                        }
//                        success()
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    override fun createGame(newGame: Game, success: () -> Unit, failure: (ErrorModel) -> Unit) {
        val game = newGame.copy(id = getNextId())
        insertGameDatabase(game)
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.createGame(newGame))) {
//                    is RequestResult.Success -> loadGames(success, failure)
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    override fun setGame(
        game: Game,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        updateGameDatabase(game)
        success(game)
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.setGame(game.id, game))) {
//                    is RequestResult.JsonSuccess -> {
//                        updateGameDatabase(response.body)
//                        success(response.body)
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    override fun deleteGame(game: Game, success: () -> Unit, failure: (ErrorModel) -> Unit) {
        deleteGameDatabase(game)
        success()
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.deleteGame(game.id))) {
//                    is RequestResult.Success -> {
//                        deleteGameDatabase(game)
//                        success()
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    override fun getGamesDatabase(
        filters: FilterModel?,
        name: String?,
        sortKey: String?,
        ascending: Boolean
    ): List<Game> {

        var queryString = "SELECT * FROM Game"
        var queryConditions = Constants.EMPTY_VALUE

        filters?.let { filtersVar ->

            if (queryConditions.isEmpty()) queryConditions += " WHERE "

            var queryPlatforms = Constants.EMPTY_VALUE
            val platforms = filtersVar.platforms
            if (platforms.isNotEmpty()) {
                queryPlatforms += "("
                for (platform in platforms) {
                    queryPlatforms += "platform == '${platform}' OR "
                }
                queryPlatforms = queryPlatforms.dropLast(4) + ") AND "
            }

            var queryGenres = Constants.EMPTY_VALUE
            val genres = filtersVar.genres
            if (genres.isNotEmpty()) {
                queryGenres += "("
                for (genre in genres) {
                    queryGenres += "genre == '${genre}' OR "
                }
                queryGenres = queryGenres.dropLast(4) + ") AND "
            }

            var queryFormats = Constants.EMPTY_VALUE
            val formats = filtersVar.formats
            if (formats.isNotEmpty()) {
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

        if (!name.isNullOrBlank()) {
            if (queryConditions.isEmpty()) queryConditions += " WHERE "

            queryConditions += "name LIKE '%$name%' AND "
        }

        queryConditions = queryConditions.dropLast(5)
        queryString += queryConditions

        queryString += " ORDER BY "
        sortKey.let {
            val order = if (ascending) "ASC" else "DESC"
            queryString += "$sortKey $order, "
        }
        queryString += "name ASC"

        val query = SimpleSQLiteQuery(queryString)

        var games: List<GameWithSaga> = arrayListOf()
        runBlocking {
            val result = databaseScope.async {
                gameDao.getGames(query)
            }
            games = result.await()
        }
        val result = ArrayList<Game>()
        for (game in games) {
            result.add(game.transform().toDomain())
        }
        return result
    }

    override fun getGameDatabase(gameId: Int): Game? {

        var game: GameWithSaga? = null
        runBlocking {
            val result = databaseScope.async {
                gameDao
                    .getGames(SimpleSQLiteQuery("SELECT * FROM Game WHERE id == '${gameId}'"))
                    .firstOrNull()
            }
            game = result.await()
        }
        return game?.transform()?.toDomain()
    }

    override fun insertGameDatabase(game: Game) {

        runBlocking {
            val job = databaseScope.launch {
                gameDao.insertGame(game.toLocalData())
            }
            job.join()
        }
    }

    override fun updateGameDatabase(game: Game) {

        runBlocking {
            val job = databaseScope.launch {
                gameDao.updateGame(game.toLocalData())
            }
            job.join()
        }
    }

    override fun removeSagaFromGames(saga: Saga) {

        val newSagaGames = saga.games
        val allGames = getGamesDatabase()
        val oldSagaGames = allGames.filter { it.saga?.id == saga.id }

        for (oldSagaGame in oldSagaGames) {
            if (newSagaGames.firstOrNull { it.id == oldSagaGame.id } == null) {

                val game = oldSagaGame.copy(saga = null)
                updateGameDatabase(game)
            }
        }

        val sagaVar = saga.copy(games = emptyList())
        for (newSagaGame in newSagaGames) {

            val game = newSagaGame.copy(saga = sagaVar)
            updateGameDatabase(game)
        }
    }

    override fun updateSagaGames(saga: Saga) {

        val sagaVar = saga.copy(games = emptyList())
        for (newGame in saga.games) {

            val game = newGame.copy(saga = sagaVar)
            updateGameDatabase(game)
        }
    }

    override fun updateGameSongs(
        game: Game,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
//        externalScope.launch {
//
//            try {
//                when (val response = ApiManager.validateResponse(api.getGame(gameId))) {
//                    is RequestResult.JsonSuccess -> {
//                        updateGameDatabase(response.body)
//                        success(response.body)
//                    }
//                    is RequestResult.Failure -> failure(response.error)
//                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//                }
//            } catch (e: Exception) {
//                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//            }
//        }
    }

    override fun resetTable() {

        val games = getGamesDatabase()
        for (game in games) {
            deleteGameDatabase(game)
        }
    }

    override fun getRawgGames(
        page: Int,
        query: String?,
        success: (List<Game>, Int, Boolean) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        externalScope.launch {

            val params: MutableMap<String, String> = HashMap()
            params[ApiManager.KEY_PARAM] = BuildConfig.RAWG_API_KEY
            params[ApiManager.PAGE_PARAM] = page.toString()
            params[ApiManager.PAGE_SIZE_PARAM] = ApiManager.PAGE_SIZE.toString()
            query?.let {
                params[ApiManager.SEARCH_PARAM] = it
            }

            try {
                when (val response = ApiManager.validateResponse(apiRawg.getGames(params))) {
                    is RequestResult.JsonSuccess -> {
                        val games = mapRawgGames(response.body.results).map { it.toDomain() }
                        success(games, response.body.count, response.body.next != null)
                    }

                    is RequestResult.Failure -> failure(response.error.toDomain())
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server).toDomain())
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection).toDomain())
            }
        }
    }

    override fun getRawgGame(
        gameId: Int,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        externalScope.launch {

            val params: MutableMap<String, String> = HashMap()
            params[ApiManager.KEY_PARAM] = BuildConfig.RAWG_API_KEY

            try {
                when (val response = ApiManager.validateResponse(apiRawg.getGame(gameId, params))) {
                    is RequestResult.JsonSuccess -> success(GameResponse(response.body).toDomain())
                    is RequestResult.Failure -> failure(response.error.toDomain())
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server).toDomain())
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection).toDomain())
            }
        }
    }
    //endregion

    //region Private methods
    private fun deleteGameDatabase(game: Game) {

        runBlocking {
            val job = databaseScope.launch {
                gameDao.deleteGame(game.toLocalData())
            }
            job.join()
        }
    }

    private fun mapRawgGames(rawgGames: List<RawgGameResponse>?): List<GameResponse> {

        val games = mutableListOf<GameResponse>()
        rawgGames?.let {
            for (rawgGame in it) {
                games.add(GameResponse(rawgGame))
            }
        }
        return games
    }

    private fun getNextId(): Int {

        val games = getGamesDatabase()
        return if (games.isNotEmpty()) {
            games.maxOf { it.id } + 1
        } else {
            0
        }
    }
    //endregion
}