package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.FilterModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga

interface GameRepository {
    suspend fun createGame(newGame: Game)
    suspend fun setGame(game: Game): Game
    suspend fun deleteGame(game: Game)
    suspend fun getGamesDatabase(
        filters: FilterModel? = null,
        name: String? = null,
        sortKey: String? = null,
        ascending: Boolean = true,
    ): List<Game>
    suspend fun getGameDatabase(gameId: Int): Game?
    suspend fun insertGameDatabase(game: Game)
    suspend fun updateGameDatabase(game: Game)
    suspend fun removeSagaFromGames(saga: Saga)
    suspend fun updateSagaGames(saga: Saga)
    suspend fun resetTable()
    suspend fun getRawgGames(
        page: Int,
        query: String?,
        success: (List<Game>, Int, Boolean) -> Unit,
        failure: (ErrorModel) -> Unit,
    )
    suspend fun getRawgGame(gameId: Int, success: (Game) -> Unit, failure: (ErrorModel) -> Unit)
    fun fetchRemoteConfigValues(language: String)
}