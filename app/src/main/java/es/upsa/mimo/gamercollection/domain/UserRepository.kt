package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel

interface UserRepository {
    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorModel) -> Unit
    )

    fun logout()

    fun updatePassword(password: String, success: () -> Unit, failure: (ErrorModel) -> Unit)

    fun deleteUser(success: () -> Unit, failure: (ErrorModel) -> Unit)
}