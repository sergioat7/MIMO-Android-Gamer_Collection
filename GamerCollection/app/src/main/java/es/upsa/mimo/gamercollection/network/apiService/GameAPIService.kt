package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.GameResponse
import retrofit2.Call
import retrofit2.http.*

interface GameAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("games")
    fun getGames(@HeaderMap headers: Map<String, String>): Call<List<GameResponse>>

    @Headers(
        "Accept:application/json"
    )
    @GET("game/{gameId}")
    fun getGame(
        @HeaderMap headers: Map<String, String>,
        @Path(value = "gameId") gameId: Int
    ): Call<GameResponse>

    @Headers(
        "Content-Type:application/json"
    )
    @POST("game")
    fun createGame(@HeaderMap headers: Map<String, String>, @Body body: GameResponse): Call<Void>

    @Headers(
        "Content-Type:application/json"
    )
    @PATCH("game/{gameId}")
    fun setGame(
        @HeaderMap headers: Map<String, String>,
        @Path(value = "gameId") gameId: Int,
        @Body body: GameResponse
    ): Call<GameResponse>

    @DELETE("game/{gameId}")
    fun deleteGame(
        @HeaderMap headers: Map<String, String>,
        @Path(value = "gameId") gameId: Int
    ): Call<Void>
}