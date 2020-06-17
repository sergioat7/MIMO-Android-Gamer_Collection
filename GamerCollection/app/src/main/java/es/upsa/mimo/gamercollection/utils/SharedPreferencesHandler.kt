package es.upsa.mimo.gamercollection.utils

import android.content.Context
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.UserData
import java.util.*

class SharedPreferencesHandler(context: Context?) {

    private val sharedPref = context?.getSharedPreferences(Constants.preferencesName, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun isLoggedIn(): Boolean {

        val userData = getUserData()
        val authData = getCredentials()
        return userData.isLoggedIn && authData.token.isNotEmpty()
    }

    fun getUserData(): UserData {

        val userDataJson = sharedPref?.getString(Constants.userDataPrefName,null)
        return if (userDataJson != null) {
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            UserData("", "", false)
        }
    }

    fun storeUserData(userData: UserData) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                val userDataJson = gson.toJson(userData)
                putString(Constants.userDataPrefName, userDataJson)
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
        sharedPref?.edit()?.remove(Constants.userDataPrefName)?.apply()
    }

    fun removePassword() {

        val userData = getUserData()
        userData.password = ""
        userData.isLoggedIn = false
        storeUserData(userData)
    }

    fun getCredentials(): AuthData {

        val authDataJson = sharedPref?.getString(Constants.authDataPrefName,null)
        return if (authDataJson != null) {
            gson.fromJson(authDataJson, AuthData::class.java)
        } else {
            AuthData("")
        }
    }

    fun storeCredentials(authData: AuthData) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                val authDataJson = gson.toJson(authData)
                putString(Constants.authDataPrefName, authDataJson)
                commit()
            }
        }
    }

    fun removeCredentials() {
        sharedPref?.edit()?.remove(Constants.authDataPrefName)?.apply()
    }

    fun getLanguage(): String {

        sharedPref?.getString(Constants.languagePrefName, null)?.let {
            return it
        } ?: run {
            val locale = Locale.getDefault().language
            setLanguage(locale)
            return locale
        }
    }

    fun setLanguage(language: String) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                putString(Constants.languagePrefName, language)
                commit()
            }
        }
    }

    fun getSortingKey(): String {
        return sharedPref?.getString(Constants.sortingKeyPrefName, null) ?: "name"
    }

    fun setSortingKey(sortingKey: String) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                putString(Constants.sortingKeyPrefName, sortingKey)
                commit()
            }
        }
    }

    fun getSwipeRefresh(): Boolean {
        return sharedPref?.getBoolean(Constants.swipeRefreshPrefName, true) ?: true
    }

    fun setSwipeRefresh(swipeRefresh: Boolean) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                putBoolean(Constants.swipeRefreshPrefName, swipeRefresh)
                commit()
            }
        }
    }

    fun notificationLaunched(gameId: Int): Boolean {
        return sharedPref?.getBoolean("${Constants.gameNotificationPrefName}${gameId}", false) ?: false
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                putBoolean("${Constants.gameNotificationPrefName}$gameId", value)
                commit()
            }
        }
    }
}