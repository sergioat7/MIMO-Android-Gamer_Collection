package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/login")
    fun login(@Body body: LoginCredentials): Call<LoginResponse>

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/register")
    fun register(@Body body: LoginCredentials): Call<Void>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("users/logout")
    fun logout(): Call<Void>

    @Headers(
        "Content-Type:application/json",
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @PUT("users/updatePassword")
    fun updatePassword(@Body body: NewPassword): Call<Void>

    @Headers(
        "${ApiManager.AUTHORIZATION_HEADER}:_"
    )
    @DELETE("users/user")
    fun deleteUser(): Call<Void>
}