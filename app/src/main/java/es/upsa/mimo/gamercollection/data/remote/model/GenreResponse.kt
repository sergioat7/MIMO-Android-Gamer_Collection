package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(
    @SerialName("id")
    @Required
    override val id: String,
    @SerialName("name")
    val name: String,
) : BaseResponse<String>

var GENRES = listOf<GenreResponse>()