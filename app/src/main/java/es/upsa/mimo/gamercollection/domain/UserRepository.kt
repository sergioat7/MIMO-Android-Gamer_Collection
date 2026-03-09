package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.data.local.model.AuthData
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.domain.model.ErrorModel

interface UserRepository {
    val language: String
    val username: String
    val userData: UserData
    val isLoggedIn: Boolean
    val sortParam: String
    val isSortOrderAscending: Boolean
    val swipeRefresh: Boolean
    val themeMode: Int
    val dateFormatToShow: String
    val filterDateFormat: String
    val newChangesPopupShown: Boolean

    fun login(
        username: String,
        password: String,
        success: (String) -> Unit,
        failure: (ErrorModel) -> Unit,
    )

    fun register(
        username: String,
        password: String,
        success: () -> Unit,
        failure: (ErrorModel) -> Unit,
    )
    suspend fun isThereMandatoryUpdate(): Boolean
    fun storeLanguage(value: String)
    fun storeCredentials(value: AuthData)
    fun storeUserData(value: UserData)
    fun storeSortParam(value: String)
    fun storeIsSortOrderAscending(value: Boolean)
    fun storeSwipeRefresh(value: Boolean)
    fun storeThemeMode(value: Int)
    fun storeNewChangesPopupShown(value: Boolean)
    fun removeCredentials()
    fun storePassword(password: String)
    fun logout()
    fun removeUserData()
    fun isNotificationLaunched(gameId: Int): Boolean
    fun setNotificationLaunched(gameId: Int, value: Boolean)
}