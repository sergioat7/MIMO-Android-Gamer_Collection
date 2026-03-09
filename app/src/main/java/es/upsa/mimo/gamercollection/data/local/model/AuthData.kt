package es.upsa.mimo.gamercollection.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("token")
    var token: String,
)