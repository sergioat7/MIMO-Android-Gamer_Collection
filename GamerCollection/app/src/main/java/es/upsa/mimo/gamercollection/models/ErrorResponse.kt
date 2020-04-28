package es.upsa.mimo.gamercollection.models

import com.google.gson.annotations.SerializedName

class ErrorResponse(
    @SerializedName("error")
    val error: String
)