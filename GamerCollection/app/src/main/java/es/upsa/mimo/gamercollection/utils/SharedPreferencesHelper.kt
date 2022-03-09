package es.upsa.mimo.gamercollection.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.extensions.setBoolean
import es.upsa.mimo.gamercollection.extensions.setInt
import es.upsa.mimo.gamercollection.extensions.setString
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.login.UserData
import java.util.*

object SharedPreferencesHelper {

    //region Private properties
    private val appPreferences = GamerCollectionApplication.context.getSharedPreferences(
        Preferences.PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val editor = appPreferences.edit()
    private val appEncryptedPreferences = EncryptedSharedPreferences.create(
        Preferences.ENCRYPTED_PREFERENCES_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        GamerCollectionApplication.context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val encryptedEditor = appEncryptedPreferences.edit()
    private val gson = Gson()
    //endregion

    //region Public methods
    fun isLoggedIn(): Boolean {

        val userData = getUserData()
        val authData = getCredentials()
        return userData.isLoggedIn && authData.token.isNotEmpty()
    }

    fun getUserData(): UserData {

        val userDataJson =
            appEncryptedPreferences.getString(Preferences.USER_DATA_PREFERENCES_NAME, null)
        return if (userDataJson != null) {
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            UserData(Constants.EMPTY_VALUE, Constants.EMPTY_VALUE, false)
        }
    }

    fun storeUserData(userData: UserData) {
        encryptedEditor.setString(Preferences.USER_DATA_PREFERENCES_NAME, gson.toJson(userData))
    }

    fun storePassword(password: String) {

        val userData = getUserData()
        storeUserData(UserData(userData.username, password, userData.isLoggedIn))
    }

    fun removeUserData() {
        encryptedEditor.remove(Preferences.USER_DATA_PREFERENCES_NAME)?.apply()
    }

    fun removePassword() {

        val userData = getUserData()
        storeUserData(UserData(userData.username, Constants.EMPTY_VALUE, false))
    }

    fun getCredentials(): AuthData {

        val authDataJson =
            appEncryptedPreferences.getString(Preferences.AUTH_DATA_PREFERENCES_NAME, null)
        return if (authDataJson != null) {
            gson.fromJson(authDataJson, AuthData::class.java)
        } else {
            AuthData(Constants.EMPTY_VALUE)
        }
    }

    fun storeCredentials(authData: AuthData) {
        encryptedEditor.setString(Preferences.AUTH_DATA_PREFERENCES_NAME, gson.toJson(authData))
    }

    fun removeCredentials() {
        encryptedEditor.remove(Preferences.AUTH_DATA_PREFERENCES_NAME)?.apply()
    }

    fun getLanguage(): String {

        appPreferences.getString(Preferences.LANGUAGE_PREFERENCES_NAME, null)?.let {
            return it
        } ?: run {
            val locale = Locale.getDefault().language
            setLanguage(locale)
            return locale
        }
    }

    fun setLanguage(language: String) {
        editor.setString(Preferences.LANGUAGE_PREFERENCES_NAME, language)
    }

    fun getDateFormatToShow(): String {

        return when (getLanguage()) {
            Preferences.SPANISH_LANGUAGE_KEY -> "d MMMM yyyy"
            else -> "MMMM d, yyyy"
        }
    }

    fun getFilterDateFormat(): String {

        return when (getLanguage()) {
            Preferences.SPANISH_LANGUAGE_KEY -> "dd/MM/yyyy"
            else -> "MM/dd/yyyy"
        }
    }

    fun getSortingKey(): String {
        return appPreferences.getString(Preferences.SORTING_KEY_PREFERENCES_NAME, null)
            ?: Preferences.DEFAULT_SORTING_KEY
    }

    fun setSortingKey(sortingKey: String) {
        editor.setString(Preferences.SORTING_KEY_PREFERENCES_NAME, sortingKey)
    }

    fun getSwipeRefresh(): Boolean {
        return appPreferences.getBoolean(Preferences.SWIPE_REFRESH_PREFERENCES_NAME, true)
    }

    fun setSwipeRefresh(swipeRefresh: Boolean) {
        editor.setBoolean(Preferences.SWIPE_REFRESH_PREFERENCES_NAME, swipeRefresh)
    }

    fun notificationLaunched(gameId: Int): Boolean {
        return appPreferences.getBoolean(
            "${Preferences.GAME_NOTIFICATION_PREFERENCES_NAME}${gameId}",
            false
        )
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {
        editor.setBoolean("${Preferences.GAME_NOTIFICATION_PREFERENCES_NAME}$gameId", value)
    }

    fun getVersion(): Int {
        return appPreferences.getInt(Preferences.VERSION_PREFERENCE_NAME, 0)
    }

    fun setVersion(version: Int) {
        editor.setInt(Preferences.VERSION_PREFERENCE_NAME, version)
    }

    fun getThemeMode(): Int {
        return appPreferences.getInt(Preferences.THEME_MODE_PREFERENCE_NAME, 0)
    }

    fun setThemeMode(themeMode: Int) {
        editor.setInt(Preferences.THEME_MODE_PREFERENCE_NAME, themeMode)
    }
    //endregion
}