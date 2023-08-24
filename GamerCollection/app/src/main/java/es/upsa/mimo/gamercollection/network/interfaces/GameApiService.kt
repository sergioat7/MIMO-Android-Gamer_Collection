package es.upsa.mimo.gamercollection.network.interfaces

import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("games")
    suspend fun getGames(): Response<List<GameResponse>>

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("game/{gameId}")
    suspend fun getGame(@Path(value = "gameId") gameId: Int): Response<GameResponse>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("game")
    suspend fun createGame(@Body body: GameResponse): Response<Unit>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PATCH("game/{gameId}")
    suspend fun setGame(
        @Path(value = "gameId") gameId: Int,
        @Body body: GameResponse
    ): Response<GameResponse>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("game/{gameId}")
    suspend fun deleteGame(@Path(value = "gameId") gameId: Int): Response<Unit>
}