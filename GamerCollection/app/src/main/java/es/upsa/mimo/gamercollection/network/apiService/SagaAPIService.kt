package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.SagaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers

interface SagaAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("sagas")
    fun getSagas(@HeaderMap headers: Map<String, String>): Call<List<SagaResponse>>
}