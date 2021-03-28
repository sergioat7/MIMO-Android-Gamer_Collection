package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.responses.SagaResponse
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

    @Headers(
        "Content-Type:application/json"
    )
    @PATCH("saga/{sagaId}")
    fun setSaga(
        @HeaderMap headers: Map<String, String>,
        @Path(value = "sagaId") sagaId: Int,
        @Body body: SagaResponse
    ): Call<SagaResponse>

    @DELETE("saga/{sagaId}")
    fun deleteSaga(
        @HeaderMap headers: Map<String, String>,
        @Path(value = "sagaId") sagaId: Int
    ): Call<Void>
}