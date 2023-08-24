package es.upsa.mimo.gamercollection.network.interfaces

import es.upsa.mimo.gamercollection.models.RawgGameListResponse
import es.upsa.mimo.gamercollection.models.RawgGameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RawgGameApiService {

    @Headers("User-Agent:GamerCollection")
    @GET("games")
    suspend fun getGames(@QueryMap queryParams: Map<String, String>): Response<RawgGameListResponse>

    @Headers("User-Agent:GamerCollection")
    @GET("games/{gameId}")
    suspend fun getGame(
        @Path(value = "gameId") gameId: Int,
        @QueryMap queryParams: Map<String, String>
    ): Response<RawgGameResponse>
}