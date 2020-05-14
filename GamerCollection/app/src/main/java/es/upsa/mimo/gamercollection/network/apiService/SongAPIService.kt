package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.SongResponse
import retrofit2.Call
import retrofit2.http.*

interface SongAPIService {

    @Headers(
        "Content-Type:application/json"
    )
    @POST("songs/{gameId}")
    fun createSong(@HeaderMap headers: Map<String, String>, @Path(value = "gameId") gameId: Int, @Body body: SongResponse): Call<Void>

    @DELETE("songs/{gameId}/{songId}")
    fun deleteSong(@HeaderMap headers: Map<String, String>, @Path(value = "gameId") gameId: Int, @Path(value = "songId") songId: Int): Call<Void>
}