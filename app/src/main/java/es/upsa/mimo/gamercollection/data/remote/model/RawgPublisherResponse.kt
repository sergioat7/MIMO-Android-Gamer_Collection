package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawgPublisherResponse(
    @SerialName("name")
    val name: String,
)
