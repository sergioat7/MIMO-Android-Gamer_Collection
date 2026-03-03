package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatformResponse(
    @SerialName("id")
    @Required
    override val id: String,
    @SerialName("name")
    val name: String,
) : BaseResponse<String>

var PLATFORMS = listOf<PlatformResponse>()