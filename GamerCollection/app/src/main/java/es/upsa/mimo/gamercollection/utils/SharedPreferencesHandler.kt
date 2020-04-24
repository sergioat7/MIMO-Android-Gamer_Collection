package es.upsa.mimo.gamercollection.utils

import android.content.Context
import android.content.SharedPreferences
import es.upsa.mimo.gamercollection.models.UserData

class SharedPreferencesHandler(context: Context?) {

    private var sharedPref: SharedPreferences? = null

    init {
        this.sharedPref = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    fun storeUserData(userData: UserData) {

        val username = userData.username
        val password = userData.password
        if (sharedPref != null) {
            with (sharedPref!!.edit()) {
                putString("username", username)
                putString("password", password)
                commit()
            }
        }
    }

    fun getUserData(): UserData {

        val username = sharedPref?.getString("username", "") ?: ""
        val password = sharedPref?.getString("password", "") ?: ""
        val isLoggedIn = !username.isEmpty() && !password.isEmpty()
        return UserData(username, password, isLoggedIn)
    }
}