package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.LoginCredentials
import es.upsa.mimo.gamercollection.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserAPIService {

    @Headers(
        "Content-Type:application/json"
    )
    @POST("users/login")
    fun login(@HeaderMap headers: Map<String, String>, @Body body: LoginCredentials): Call<LoginResponse>
}