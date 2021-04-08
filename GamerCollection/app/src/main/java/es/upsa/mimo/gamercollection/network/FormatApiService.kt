package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface FormatApiService {

    @Headers(
        "Accept:application/json"
    )
    @GET("formats")
    suspend fun getFormats(): Response<List<FormatResponse>>
}