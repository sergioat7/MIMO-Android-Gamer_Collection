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

    //region Public properties
    var language: String
        get() {
            return appPreferences.getString(Preferences.LANGUAGE_PREFERENCES_NAME, null) ?: run {
                language = Locale.getDefault().language
                language
            }
        }
        set(value) = editor.setString(Preferences.LANGUAGE_PREFERENCES_NAME, value)
    var credentials: AuthData
        get() {
            return appEncryptedPreferences.getString(Preferences.AUTH_DATA_PREFERENCES_NAME, null)
                ?.let {
                    gson.fromJson(it, AuthData::class.java)
                } ?: run {
                AuthData(Constants.EMPTY_VALUE)
            }
        }
        set(value) = encryptedEditor.setString(
            Preferences.AUTH_DATA_PREFERENCES_NAME,
            gson.toJson(value)
        )
    var userData: UserData
        get() {
            return appEncryptedPreferences.getString(Preferences.USER_DATA_PREFERENCES_NAME, null)
                ?.let {
                    gson.fromJson(it, UserData::class.java)
                } ?: run {
                UserData(Constants.EMPTY_VALUE, Constants.EMPTY_VALUE, false)
            }
        }
        set(value) = encryptedEditor.setString(
            Preferences.USER_DATA_PREFERENCES_NAME,
            gson.toJson(value)
        )
    val isLoggedIn: Boolean
        get() = userData.isLoggedIn && credentials.token.isNotEmpty()
    var sortParam: String
        get() = appPreferences.getString(Preferences.SORT_PARAM_PREFERENCES_NAME, null)
            ?: Preferences.DEFAULT_SORT_PARAM
        set(value) = editor.setString(Preferences.SORT_PARAM_PREFERENCES_NAME, value)
    var isSortOrderAscending: Boolean
        get() = appPreferences.getBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, false)
        set(value) = editor.setBoolean(Preferences.SORT_ORDER_PREFERENCE_NAME, value)
    var swipeRefresh: Boolean
        get() = appPreferences.getBoolean(Preferences.SWIPE_REFRESH_PREFERENCES_NAME, true)
        set(value) = editor.setBoolean(Preferences.SWIPE_REFRESH_PREFERENCES_NAME, value)
    var version: Int
        get() = appPreferences.getInt(Preferences.VERSION_PREFERENCES_NAME, 0)
        set(value) = editor.setInt(Preferences.VERSION_PREFERENCES_NAME, value)
    var themeMode: Int
        get() = appPreferences.getInt(Preferences.THEME_MODE_PREFERENCES_NAME, 0)
        set(value) = editor.setInt(Preferences.THEME_MODE_PREFERENCES_NAME, value)
    val dateFormatToShow: String
        get() {
            return when (language) {
                Preferences.SPANISH_LANGUAGE_KEY -> "d MMMM yyyy"
                else -> "MMMM d, yyyy"
            }
        }
    val filterDateFormat: String
        get() {
            return when (language) {
                Preferences.SPANISH_LANGUAGE_KEY -> "dd/MM/yyyy"
                else -> "MM/dd/yyyy"
            }
        }
    //endregion

    //region Public methods
    fun removeCredentials() {
        encryptedEditor.remove(Preferences.AUTH_DATA_PREFERENCES_NAME)?.apply()
    }

    fun storePassword(password: String) {
        userData = UserData(userData.username, password, userData.isLoggedIn)
    }

    fun removePassword() {
        userData = UserData(userData.username, Constants.EMPTY_VALUE, false)
    }

    fun removeUserData() {
        encryptedEditor.remove(Preferences.USER_DATA_PREFERENCES_NAME)?.apply()
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
    //endregion
}