package es.upsa.mimo.gamercollection.data.local.model

data class UserData(
    var username: String,
    var password: String,
    var isLoggedIn: Boolean,
)