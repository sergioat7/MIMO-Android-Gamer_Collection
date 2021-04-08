package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import retrofit2.Call
import retrofit2.http.*

interface SagaApiService {

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("sagas")
    fun getSagas(): Call<List<SagaResponse>>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("saga")
    fun createSaga(@Body body: SagaResponse): Call<Void>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PATCH("saga/{sagaId}")
    fun setSaga(
        @Path(value = "sagaId") sagaId: Int,
        @Body body: SagaResponse
    ): Call<SagaResponse>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("saga/{sagaId}")
    fun deleteSaga(@Path(value = "sagaId") sagaId: Int): Call<Void>
}