package es.upsa.mimo.gamercollection.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.UserData
import java.util.*
import javax.inject.Inject

class SharedPreferencesHandler @Inject constructor(
    private val sharedPreferences: SharedPreferences?
) {

    // MARK: - Private properties

    private val gson = Gson()

    // MARK: - Public methods

    fun isLoggedIn(): Boolean {

        val userData = getUserData()
        val authData = getCredentials()
        return userData.isLoggedIn && authData.token.isNotEmpty()
    }

    fun getUserData(): UserData {

        val userDataJson = sharedPreferences?.getString(Constants.USER_DATA_PREFERENCES_NAME,null)
        return if (userDataJson != null) {
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            UserData("", "", false)
        }
    }

    fun storeUserData(userData: UserData) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                val userDataJson = gson.toJson(userData)
                putString(Constants.USER_DATA_PREFERENCES_NAME, userDataJson)
                commit()
            }
        }
    }

    fun storePassword(password: String) {

        val userData = getUserData()
        userData.password = password
        storeUserData(userData)
    }

    fun removeUserData() {
        sharedPreferences?.edit()?.remove(Constants.USER_DATA_PREFERENCES_NAME)?.apply()
    }

    fun removePassword() {

        val userData = getUserData()
        userData.password = ""
        userData.isLoggedIn = false
        storeUserData(userData)
    }

    fun getCredentials(): AuthData {

        val authDataJson = sharedPreferences?.getString(Constants.AUTH_DATA_PREFERENCES_NAME,null)
        return if (authDataJson != null) {
            gson.fromJson(authDataJson, AuthData::class.java)
        } else {
            AuthData("")
        }
    }

    fun storeCredentials(authData: AuthData) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                val authDataJson = gson.toJson(authData)
                putString(Constants.AUTH_DATA_PREFERENCES_NAME, authDataJson)
                commit()
            }
        }
    }

    fun removeCredentials() {
        sharedPreferences?.edit()?.remove(Constants.AUTH_DATA_PREFERENCES_NAME)?.apply()
    }

    fun getLanguage(): String {

        sharedPreferences?.getString(Constants.LANGUAGE_PREFERENCES_NAME, null)?.let {
            return it
        } ?: run {
            val locale = Locale.getDefault().language
            setLanguage(locale)
            return locale
        }
    }

    fun setLanguage(language: String) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                putString(Constants.LANGUAGE_PREFERENCES_NAME, language)
                commit()
            }
        }
    }

    fun getSortingKey(): String {
        return sharedPreferences?.getString(Constants.SORTING_KEY_PREFERENCES_NAME, null) ?: "name"
    }

    fun setSortingKey(sortingKey: String) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                putString(Constants.SORTING_KEY_PREFERENCES_NAME, sortingKey)
                commit()
            }
        }
    }

    fun getSwipeRefresh(): Boolean {
        return sharedPreferences?.getBoolean(Constants.SWIPE_REFRESH_PREFERENCES_NAME, true) ?: true
    }

    fun setSwipeRefresh(swipeRefresh: Boolean) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                putBoolean(Constants.SWIPE_REFRESH_PREFERENCES_NAME, swipeRefresh)
                commit()
            }
        }
    }

    fun notificationLaunched(gameId: Int): Boolean {
        return sharedPreferences?.getBoolean("${Constants.GAME_NOTIFICATION_PREFERENCES_NAME}${gameId}", false) ?: false
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {

        if (sharedPreferences != null) {
            with (sharedPreferences.edit()) {
                putBoolean("${Constants.GAME_NOTIFICATION_PREFERENCES_NAME}$gameId", value)
                commit()
            }
        }
    }
}