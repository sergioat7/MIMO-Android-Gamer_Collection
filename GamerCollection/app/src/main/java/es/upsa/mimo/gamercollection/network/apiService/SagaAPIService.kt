package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.SagaResponse
import retrofit2.Call
import retrofit2.http.*

interface SagaAPIService {

    @Headers(
        "Accept:application/json"
    )
    @GET("sagas")
    fun getSagas(@HeaderMap headers: Map<String, String>): Call<List<SagaResponse>>

    @Headers(
        "Content-Type:application/json"
    )
    @POST("saga")
    fun createSaga(@HeaderMap headers: Map<String, String>, @Body body: SagaResponse): Call<Void>
}