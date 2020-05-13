package es.upsa.mimo.gamercollection.network.apiService

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface SongAPIService {

    @DELETE("songs/{gameId}/{songId}")
    fun deleteSong(@HeaderMap headers: Map<String, String>, @Path(value = "gameId") gameId: Int, @Path(value = "songId") songId: Int): Call<Void>
}