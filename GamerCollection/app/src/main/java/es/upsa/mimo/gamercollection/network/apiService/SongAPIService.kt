package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.apiClient.ApiManager
import retrofit2.Call
import retrofit2.http.*

interface SongAPIService {

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("songs/{gameId}")
    fun createSong(
        @Path(value = "gameId") gameId: Int,
        @Body body: SongResponse
    ): Call<Void>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("songs/{gameId}/{songId}")
    fun deleteSong(
        @Path(value = "gameId") gameId: Int,
        @Path(value = "songId") songId: Int
    ): Call<Void>
}