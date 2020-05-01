package es.upsa.mimo.gamercollection.models

import com.google.gson.annotations.SerializedName

data class SagaResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("games")
    val games: List<GameResponse>
)