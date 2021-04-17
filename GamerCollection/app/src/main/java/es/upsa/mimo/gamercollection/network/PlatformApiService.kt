package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface PlatformApiService {

    @Headers(
        "Accept:application/json"
    )
    @GET("platforms")
    suspend fun getPlatforms(): Response<List<PlatformResponse>>
}