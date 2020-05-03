package es.upsa.mimo.gamercollection.network.apiService

import es.upsa.mimo.gamercollection.models.LoginCredentials
import es.upsa.mimo.gamercollection.models.LoginResponse
import es.upsa.mimo.gamercollection.models.NewPassword
import retrofit2.Call
import retrofit2.http.*

interface UserAPIService {

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/login")
    fun login(@HeaderMap headers: Map<String, String>, @Body body: LoginCredentials): Call<LoginResponse>

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/register")
    fun register(@HeaderMap headers: Map<String, String>, @Body body: LoginCredentials): Call<Void>

    @Headers(
        "Content-Type:application/json"
    )
    @PUT("users/updatePassword")
    fun updatePassword(@HeaderMap headers: Map<String, String>, @Body body: NewPassword): Call<Void>
}