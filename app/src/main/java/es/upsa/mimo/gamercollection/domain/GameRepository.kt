package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.models.FilterModel

interface GameRepository {
    fun loadGames(success: () -> Unit, failure: (ErrorModel) -> Unit)
    fun createGame(newGame: Game, success: () -> Unit, failure: (ErrorModel) -> Unit)
    fun setGame(
        game: Game,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun deleteGame(game: Game, success: () -> Unit, failure: (ErrorModel) -> Unit)
    fun getGamesDatabase(
        filters: FilterModel? = null,
        name: String? = null,
        sortKey: String? = null,
        ascending: Boolean = true
    ): List<Game>

    fun getGameDatabase(gameId: Int): Game?
    fun insertGameDatabase(game: Game)
    fun updateGameDatabase(game: Game)
    fun removeSagaFromGames(saga: Saga)
    fun updateSagaGames(saga: Saga)
    fun updateGameSongs(
        game: Game,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun resetTable()
    fun getRawgGames(
        page: Int,
        query: String?,
        success: (List<Game>, Int, Boolean) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun getRawgGame(
        gameId: Int,
        success: (Game) -> Unit,
        failure: (ErrorModel) -> Unit
    )
}