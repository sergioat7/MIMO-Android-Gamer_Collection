package es.upsa.mimo.gamercollection.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("username")
    var username: String,
    @SerialName("password")
    var password: String,
    @SerialName("isLoggedIn")
    var isLoggedIn: Boolean,
)