package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.GameResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface GameAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("games")
    fun getGames(@HeaderMap headers: Map<String, String>): Call<List<GameResponse>>

    @Headers(
        "Content-Type:application/json"
    )
    @PATCH("game/{gameId}")
    fun setGame(@HeaderMap headers: Map<String, String>, @Path(value = "gameId") gameId: Int, @Body body: GameResponse): Call<GameResponse>
}