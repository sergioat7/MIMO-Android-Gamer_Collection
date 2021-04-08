package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.GameResponse
import retrofit2.Call
import retrofit2.http.*

interface GameApiService {

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("games")
    fun getGames(): Call<List<GameResponse>>

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("game/{gameId}")
    fun getGame(@Path(value = "gameId") gameId: Int): Call<GameResponse>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("game")
    fun createGame(@Body body: GameResponse): Call<Void>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PATCH("game/{gameId}")
    fun setGame(
        @Path(value = "gameId") gameId: Int,
        @Body body: GameResponse
    ): Call<GameResponse>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("game/{gameId}")
    fun deleteGame(@Path(value = "gameId") gameId: Int): Call<Void>
}