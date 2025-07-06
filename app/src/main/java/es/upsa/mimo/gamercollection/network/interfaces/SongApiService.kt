package es.upsa.mimo.gamercollection.network.interfaces

import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import retrofit2.Response
import retrofit2.http.*

interface SongApiService {

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("songs/{gameId}")
    suspend fun createSong(
        @Path(value = "gameId") gameId: Int,
        @Body body: SongResponse
    ): Response<Unit>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("songs/{gameId}/{songId}")
    suspend fun deleteSong(
        @Path(value = "gameId") gameId: Int,
        @Path(value = "songId") songId: Int
    ): Response<Unit>
}