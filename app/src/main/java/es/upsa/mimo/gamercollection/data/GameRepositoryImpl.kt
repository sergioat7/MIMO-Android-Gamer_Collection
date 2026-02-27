package es.upsa.mimo.gamercollection.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.daos.GameDao
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import es.upsa.mimo.gamercollection.data.remote.model.BaseResponse
import es.upsa.mimo.gamercollection.data.remote.model.ErrorResponse
import es.upsa.mimo.gamercollection.data.remote.model.FORMATS
import es.upsa.mimo.gamercollection.data.remote.model.FormatResponse
import es.upsa.mimo.gamercollection.data.remote.model.GENRES
import es.upsa.mimo.gamercollection.data.remote.model.GameResponse
import es.upsa.mimo.gamercollection.data.remote.model.GenreResponse
import es.upsa.mimo.gamercollection.data.remote.model.PLATFORMS
import es.upsa.mimo.gamercollection.data.remote.model.PlatformResponse
import es.upsa.mimo.gamercollection.data.remote.model.RawgGameResponse
import es.upsa.mimo.gamercollection.data.remote.model.STATES
import es.upsa.mimo.gamercollection.data.remote.model.StateResponse
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.FilterModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.domain.toLocalData
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val apiRawg: RawgGameApiService,
    private val gameDao: GameDao,
    private val remoteConfig: FirebaseRemoteConfig,
) : GameRepository {

    //region Static properties
    companion object {
        private const val KEY_PARAM = "key"
        private const val PAGE_PARAM = "page"
        private const val PAGE_SIZE_PARAM = "page_size"
        private const val PAGE_SIZE = 20
        private const val SEARCH_PARAM = "search"
    }
    //endregion

    //region Public methods
    override suspend fun createGame(newGame: Game) {
        val game = newGame.copy(id = getNextId())
        gameDao.insertGame(game.toLocalData())
    }

    override suspend fun setGame(game: Game): Game {
        return game.also {
            gameDao.updateGame(it.toLocalData())
        }
    }

    override suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game.toLocalData())
    }

    override suspend fun getGamesDatabase(
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

            if (filtersVar.minScore > 0) {
                queryConditions += "score >= ${filtersVar.minScore} AND "
            }

            if (filtersVar.maxScore < 10) {
                queryConditions += "score <= ${filtersVar.maxScore} AND "
            }

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

            if (filtersVar.minPrice > 0) {
                queryConditions += "price >= ${filtersVar.minPrice} AND "
            }

            if (filtersVar.maxPrice > 0) {
                queryConditions += "price <= ${filtersVar.maxPrice} AND "
            }

            if (filtersVar.isGoty != null) {
                queryConditions += "goty == ${if (filtersVar.isGoty) 1 else 0} AND "
            }

            if (filtersVar.isLoaned != null) {
                queryConditions += "${if (filtersVar.isLoaned) "loanedTo is not null" else "loanedTo is null"} AND "
            }

            if (filtersVar.hasSaga != null) {
                queryConditions += "${if (filtersVar.hasSaga) "saga_id is not null" else "saga_id is null"} AND "
            }

            if (filtersVar.hasSongs != null) {
                queryConditions += "${if (filtersVar.hasSongs) "songs != '[]'" else "songs == '[]'"} AND "
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
        return gameDao.getGames(query).map { it.transform().toDomain() }
    }

    override suspend fun getGameDatabase(gameId: Int): Game? {
        return gameDao
            .getGames(SimpleSQLiteQuery("SELECT * FROM Game WHERE id == '${gameId}'"))
            .firstOrNull()
            ?.transform()
            ?.toDomain()
    }

    override suspend fun insertGameDatabase(game: Game) {
        gameDao.insertGame(game.toLocalData())
    }

    override suspend fun updateGameDatabase(game: Game) {
        gameDao.updateGame(game.toLocalData())
    }

    override suspend fun removeSagaFromGames(saga: Saga) {

        val newSagaGames = saga.games
        val oldSagaGames = gameDao
            .getGames(SimpleSQLiteQuery("SELECT * FROM Game"))
            .filter { it.saga?.id == saga.id }
            .map { it.transform() }

        for (oldSagaGame in oldSagaGames) {
            if (newSagaGames.firstOrNull { it.id == oldSagaGame.id } == null) {

                val game = oldSagaGame.copy(saga = null)
                gameDao.updateGame(game)
            }
        }

        val sagaVar = saga.copy(games = emptyList())
        for (newSagaGame in newSagaGames) {

            val game = newSagaGame.copy(saga = sagaVar)
            gameDao.updateGame(game.toLocalData())
        }
    }

    override suspend fun updateSagaGames(saga: Saga) {

        val sagaVar = saga.copy(games = emptyList())
        for (newGame in saga.games) {

            val game = newGame.copy(saga = sagaVar)
            gameDao.updateGame(game.toLocalData())
        }
    }

    override suspend fun resetTable() {

        val games = gameDao.getGames(SimpleSQLiteQuery("SELECT * FROM Game")).map { it.transform() }
        for (game in games) {
            gameDao.deleteGame(game)
        }
    }

    override suspend fun getRawgGames(
        page: Int,
        query: String?,
        success: (List<Game>, Int, Boolean) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        val params: MutableMap<String, String> = HashMap()
        params[KEY_PARAM] = BuildConfig.RAWG_API_KEY
        params[PAGE_PARAM] = page.toString()
        params[PAGE_SIZE_PARAM] = PAGE_SIZE.toString()
        query?.let {
            params[SEARCH_PARAM] = it
        }

        try {
            val response = apiRawg.getGames(params)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val games = mapRawgGames(body.results).map { it.toDomain() }
                success(games, body.count, body.next != null)
            } else {
                failure(
                    ErrorResponse(
                        error = Constants.EMPTY_VALUE,
                        errorKey = R.string.error_server,
                    ).toDomain()
                )
            }
        } catch (_: Exception) {
            failure(
                ErrorResponse(
                    error = Constants.EMPTY_VALUE,
                    errorKey = R.string.error_server_connection,
                ).toDomain()
            )
        }
    }

    override suspend fun getRawgGame(
        gameId: Int,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        val params: MutableMap<String, String> = HashMap()
        params[KEY_PARAM] = BuildConfig.RAWG_API_KEY

        try {
            val response = apiRawg.getGame(gameId, params)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                success(GameResponse(body).toDomain())
            } else {
                failure(
                    ErrorResponse(
                        error = Constants.EMPTY_VALUE,
                        errorKey = R.string.error_server,
                    ).toDomain()
                )
            }
        } catch (_: Exception) {
            failure(
                ErrorResponse(
                    error = Constants.EMPTY_VALUE,
                    errorKey = R.string.error_server_connection,
                ).toDomain()
            )
        }
    }

    override fun fetchRemoteConfigValues(language: String) {

        fetchRemoteConfigString("formats") {
            FORMATS = parseValues<FormatResponse>(it)[language] ?: emptyList()
        }

        fetchRemoteConfigString("genres") {
            GENRES = parseValues<GenreResponse>(it)[language] ?: emptyList()
        }

        fetchRemoteConfigString("platforms") {
            PLATFORMS = parseValues<PlatformResponse>(it)[language] ?: emptyList()
        }

        fetchRemoteConfigString("states") {
            STATES = parseValues<StateResponse>(it)[language] ?: emptyList()
        }
    }
    //endregion

    //region Private methods
    private fun mapRawgGames(rawgGames: List<RawgGameResponse>?): List<GameResponse> {

        val games = mutableListOf<GameResponse>()
        rawgGames?.let {
            for (rawgGame in it) {
                games.add(GameResponse(rawgGame))
            }
        }
        return games
    }

    private suspend fun getNextId(): Int {

        val games = gameDao.getGames(SimpleSQLiteQuery("SELECT * FROM Game"))
        return if (games.isNotEmpty()) {
            games.maxOf { it.game.id } + 1
        } else {
            0
        }
    }

    private fun fetchRemoteConfigString(key: String, onCompletion: (String) -> Unit) {
        onCompletion(remoteConfig.getString(key))

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            onCompletion(remoteConfig.getString(key))
        }
    }

    private inline fun <reified T : BaseResponse<String>> parseValues(
        values: String,
    ): Map<String, List<T>> = if (values.isNotEmpty()) {
        try {
            val type = object : TypeToken<Map<String, List<T>>>() {}.type
            Gson().fromJson(values, type)
        } catch (e: Exception) {
            println("GameRepository ${(e.message ?: "")}")
            emptyMap()
        }
    } else {
        emptyMap()
    }
    //endregion
}