package es.upsa.mimo.gamercollection.models

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)