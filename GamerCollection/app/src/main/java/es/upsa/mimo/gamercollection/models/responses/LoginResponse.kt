package es.upsa.mimo.gamercollection.models.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String
)