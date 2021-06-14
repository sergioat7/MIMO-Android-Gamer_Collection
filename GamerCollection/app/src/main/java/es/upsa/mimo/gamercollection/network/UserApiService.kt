package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/login")
    suspend fun login(@Body body: LoginCredentials): Response<LoginResponse>

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/register")
    suspend fun register(@Body body: LoginCredentials): Response<Unit>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("users/logout")
    suspend fun logout(): Response<Unit>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PUT("users/updatePassword")
    suspend fun updatePassword(@Body body: NewPassword): Response<Unit>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("users/user")
    suspend fun deleteUser(): Response<Unit>
}