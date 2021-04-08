package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GenreApiService {

    @Headers(
        "Accept:application/json"
    )
    @GET("genres")
    fun getGenres(): Call<List<GenreResponse>>
}