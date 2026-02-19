package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.models.ErrorResponse

interface UserRepository {
    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    fun logout()

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorResponse) -> Unit)

    fun deleteUser(success: () -> Unit, failure: (ErrorResponse) -> Unit)
}