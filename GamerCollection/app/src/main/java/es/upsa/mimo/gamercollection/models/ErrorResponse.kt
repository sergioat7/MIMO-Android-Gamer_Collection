package es.upsa.mimo.gamercollection.models

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    val error: String,
    val errorKey: Int
)