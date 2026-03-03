package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SagaResponse(
    @SerialName("id")
    @Required
    override var id: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("games")
    val games: List<GameResponse> = emptyList(),
) : BaseResponse<Int>