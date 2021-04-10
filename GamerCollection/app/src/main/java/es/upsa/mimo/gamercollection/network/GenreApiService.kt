package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface GenreApiService {

    @Headers(
        "Accept:application/json"
    )
    @GET("genres")
    suspend fun getGenres(): Response<List<GenreResponse>>
}