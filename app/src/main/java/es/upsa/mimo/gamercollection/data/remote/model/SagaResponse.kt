package es.upsa.mimo.gamercollection.data.remote.model

import com.google.gson.annotations.SerializedName

data class SagaResponse(
    @SerializedName("id")
    override var id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("games")
    val games: List<GameResponse>,
) : BaseResponse<Int>