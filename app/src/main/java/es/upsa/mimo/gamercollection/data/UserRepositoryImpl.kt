package es.upsa.mimo.gamercollection.data

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.data.remote.model.ErrorResponse
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    private val preferences: SharedPreferencesHelper,
) : UserRepository {

    //region Private properties
    private val GOOGLE_USER_TEST = "googleTest"
    private val GOOGLE_PASSWORD_TEST = "d9MqzK3k1&07"
    //endregion

    //region Public methods
    override fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorModel) -> Unit,
    ) {
        val userData = preferences.userData
        if (username == GOOGLE_USER_TEST && password == GOOGLE_PASSWORD_TEST) {
            preferences.userData = UserData(
                GOOGLE_USER_TEST,
                GOOGLE_PASSWORD_TEST,
                false,
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
        failure: (ErrorModel) -> Unit,
    ) {
        preferences.userData = UserData(username, password, false)
        success()
    }

    override suspend fun isThereMandatoryUpdate(): Boolean {
        val currentVersion = getCalculatedCurrentVersion()
        val minVersion = getCalculatedMinVersion()
        return currentVersion < minVersion
    }
    //endregion

    //region Private methods
    private fun getCalculatedCurrentVersion(): Int {
        return try {
            val currentVersion = context
                .packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
                ?.split(".")
                ?: listOf()
            if (currentVersion.size != 3) return Int.MAX_VALUE

            currentVersion[0].toInt() * 100000 +
                currentVersion[1].toInt() * 1000 +
                currentVersion[2].toInt() * 10
        } catch (_: Exception) {
            Int.MAX_VALUE
        }
    }

    private suspend fun getCalculatedMinVersion(): Int {
        try {
            remoteConfig.fetch(0)
            remoteConfig.activate().await()
        } catch (_: Exception) {
        }

        val minVersion = remoteConfig.getString("min_version").split(".")
        if (minVersion.size != 3) return 0

        return minVersion[0].toInt() * 100000 +
            minVersion[1].toInt() * 1000 +
            minVersion[2].toInt() * 10
    }
    //endregion
}