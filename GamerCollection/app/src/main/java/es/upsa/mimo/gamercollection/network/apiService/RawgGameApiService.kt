package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.rawg.RawgGameListResponse
import es.upsa.mimo.gamercollection.models.rawg.RawgGameResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RawgGameApiService {

    @Headers("User-Agent:GamerCollection")
    @GET("games")
    fun getGames(@QueryMap queryParams: Map<String, String>): Call<RawgGameListResponse>

    @Headers("User-Agent:GamerDB")
    @GET("games/{gameId}")
    fun getGame(
        @Path(value = "gameId") gameId: Int,
        @QueryMap queryParams: Map<String, String>
    ): Call<RawgGameResponse>
}