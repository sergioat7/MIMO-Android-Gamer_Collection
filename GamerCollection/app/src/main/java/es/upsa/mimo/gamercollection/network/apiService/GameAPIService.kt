package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.GameResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers

interface GameAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("games")
    fun getGames(@HeaderMap headers: Map<String, String>): Call<List<GameResponse>>
}