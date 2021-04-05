package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface PlatformAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("platforms")
    fun getPlatforms(): Call<List<PlatformResponse>>
}