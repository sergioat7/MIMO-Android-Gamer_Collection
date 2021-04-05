package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface FormatAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("formats")
    fun getFormats(): Call<List<FormatResponse>>
}