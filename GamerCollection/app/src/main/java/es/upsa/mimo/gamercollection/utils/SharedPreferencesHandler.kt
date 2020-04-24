package es.upsa.mimo.gamercollection.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.models.UserData

class SharedPreferencesHandler(context: Context?) {

    private var sharedPref: SharedPreferences? = null
    private lateinit var gson: Gson

    init {
        this.sharedPref = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        this.gson = Gson()
    }

    fun storeUserData(userData: UserData) {

        if (sharedPref != null) {
            with (sharedPref!!.edit()) {

                val userDataJson = gson.toJson(userData)
                putString("userData", userDataJson)
                commit()
            }
        }
    }

    fun getUserData(): UserData {

        val userDataJson = sharedPref?.getString("userData",null)
        return if (userDataJson != null) {
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            UserData("", "", false)
        }
    }
}