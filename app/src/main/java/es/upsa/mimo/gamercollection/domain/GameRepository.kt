package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse

interface GameRepository {
    fun loadGames(success: () -> Unit, failure: (ErrorResponse) -> Unit)
    fun createGame(newGame: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit)
    fun setGame(
        game: GameResponse,
        success: (GameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun deleteGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit)
    fun getGamesDatabase(
        filters: FilterModel? = null,
        name: String? = null,
        sortKey: String? = null,
        ascending: Boolean = true
    ): List<GameResponse>

    fun getGameDatabase(gameId: Int): GameResponse?
    fun insertGameDatabase(game: GameResponse)
    fun updateGameDatabase(game: GameResponse)
    fun removeSagaFromGames(saga: SagaResponse)
    fun updateSagaGames(saga: SagaResponse)
    fun updateGameSongs(
        game: GameResponse,
        success: (GameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun resetTable()
    fun getRawgGames(
        page: Int,
        query: String?,
        success: (List<GameResponse>, Int, Boolean) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun getRawgGame(
        gameId: Int,
        success: (GameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    )
}