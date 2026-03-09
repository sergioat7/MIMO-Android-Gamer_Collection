package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongResponse(
    @SerialName("id")
    @Required
    var id: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("singer")
    val singer: String? = null,
    @SerialName("url")
    val url: String? = null,
)