package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GenreAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("genres")
    fun getGenres(): Call<List<GenreResponse>>
}