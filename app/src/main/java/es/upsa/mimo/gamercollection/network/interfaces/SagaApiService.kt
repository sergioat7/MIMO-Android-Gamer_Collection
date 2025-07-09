package es.upsa.mimo.gamercollection.network.interfaces

import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import retrofit2.Response
import retrofit2.http.*

interface SagaApiService {

    @Headers(
        "Accept:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @GET("sagas")
    suspend fun getSagas(): Response<List<SagaResponse>>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @POST("saga")
    suspend fun createSaga(@Body body: SagaResponse): Response<Unit>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PATCH("saga/{sagaId}")
    suspend fun setSaga(
        @Path(value = "sagaId") sagaId: Int,
        @Body body: SagaResponse
    ): Response<SagaResponse>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("saga/{sagaId}")
    suspend fun deleteSaga(@Path(value = "sagaId") sagaId: Int): Response<Unit>
}