package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.GenreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers

interface GenreAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("genres")
    fun getGenres(@HeaderMap headers: Map<String, String>): Call<List<GenreResponse>>
}