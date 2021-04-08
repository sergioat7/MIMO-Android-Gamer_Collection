package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.StateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface StateApiService {

    @Headers(
        "Accept:application/json"
    )
    @GET("states")
    fun getStates(): Call<List<StateResponse>>
}