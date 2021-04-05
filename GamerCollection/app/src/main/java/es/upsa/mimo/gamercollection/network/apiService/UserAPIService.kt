package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.requests.LoginCredentials
import es.upsa.mimo.gamercollection.models.requests.NewPassword
import es.upsa.mimo.gamercollection.models.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface UserAPIService {

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

    @DELETE("users/logout")
    fun logout(@HeaderMap headers: Map<String, String>): Call<Void>

    @Headers(
        "Content-Type:application/json"
    )
    @PUT("users/updatePassword")
    fun updatePassword(@HeaderMap headers: Map<String, String>, @Body body: NewPassword): Call<Void>

    @DELETE("users/user")
    fun deleteUser(@HeaderMap headers: Map<String, String>): Call<Void>
}