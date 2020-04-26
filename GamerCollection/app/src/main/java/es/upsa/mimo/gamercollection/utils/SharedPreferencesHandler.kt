package es.upsa.mimo.gamercollection.utils

import android.content.Context
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.models.UserData

class SharedPreferencesHandler(context: Context?) {

    private val sharedPref = context?.getSharedPreferences(Constants.preferencesName, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun isNewInstallation(): Boolean {
        return sharedPref?.getBoolean(Constants.newInstallationPrefName, false) ?: false
    }

    fun setIsNewInstallation() {

        if (sharedPref != null) {
            with (sharedPref.edit()) {
                putBoolean(Constants.newInstallationPrefName, true)
                commit()
            }
        }
    }

    fun isLoggedIn(): Boolean {

        val userData = getUserData()
        //TODO getCredentials
        return userData.isLoggedIn
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

    fun storeCredentials() {
        //TODO
    }

    fun getUserData(): UserData {

        val userDataJson = sharedPref?.getString(Constants.userDataPrefName,null)
        return if (userDataJson != null) {
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            UserData("", "", false)
        }
    }

    fun getCredentials() {
        //TODO
    }

    fun removeUserData() {
        sharedPref?.edit()?.clear()?.apply()
    }

    fun removePassword() {

        val userData = getUserData()
        userData.password = ""
        userData.isLoggedIn = false
        storeUserData(userData)
    }

    fun removeCredentials() {
        //TODO
    }
}