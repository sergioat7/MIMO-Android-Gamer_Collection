package es.upsa.mimo.gamercollection.data

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.data.remote.model.ErrorResponse
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    //region Private properties
    private val GOOGLE_USER_TEST = "googleTest"
    private val GOOGLE_PASSWORD_TEST = "d9MqzK3k1&07"
    //endregion

    //region Public methods
    override fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        val userData = SharedPreferencesHelper.userData
        if (username == GOOGLE_USER_TEST && password == GOOGLE_PASSWORD_TEST) {
            SharedPreferencesHelper.userData = UserData(
                GOOGLE_USER_TEST,
                GOOGLE_PASSWORD_TEST,
                false
            )
            success("-")
        } else if (userData.username.isEmpty() || userData.username != username) {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.username_not_exist).toDomain())
        } else if (userData.username == username && userData.password == password) {
            success("-")
        } else {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.wrong_password).toDomain())
        }
    }

    override fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        SharedPreferencesHelper.userData = UserData(username, password, false)
        success()
    }
    //endregion
}