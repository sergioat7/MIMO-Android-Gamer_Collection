package es.upsa.mimo.gamercollection.models.login

data class UserData(
    var username: String,
    var password: String,
    var isLoggedIn: Boolean
)