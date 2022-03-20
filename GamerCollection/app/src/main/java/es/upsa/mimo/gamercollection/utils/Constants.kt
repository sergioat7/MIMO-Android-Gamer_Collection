package es.upsa.mimo.gamercollection.utils

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter

object Preferences {
    const val PREFERENCES_NAME = "preferences"
    const val ENCRYPTED_PREFERENCES_NAME = "encryptedPreferences"
    const val USER_DATA_PREFERENCES_NAME = "userData"
    const val AUTH_DATA_PREFERENCES_NAME = "authData"
    const val LANGUAGE_PREFERENCES_NAME = "language"
    const val ENGLISH_LANGUAGE_KEY = "en"
    const val SPANISH_LANGUAGE_KEY = "es"
    const val SORT_PARAM_PREFERENCES_NAME = "sortParam"
    const val DEFAULT_SORT_PARAM = "name"
    const val SWIPE_REFRESH_PREFERENCES_NAME = "swipeRefreshEnabled"
    const val GAME_NOTIFICATION_PREFERENCES_NAME = "gameNotificationLaunched_"
    const val VERSION_PREFERENCES_NAME = "version"
    const val THEME_MODE_PREFERENCES_NAME = "themeMode"
}

object Constants {
    const val GAME_ID = "gameId"
    const val SAGA_ID = "sagaId"
    const val IS_RAWG_GAME = "isRawgGame"
    const val LOADING_DIALOG = "loadingDialog"
    const val SYNC_DIALOG = "syncDialog"
    const val POINT_UP = 0f
    const val POINT_DOWN = -180f
    const val DATABASE_NAME = "GamerCollection"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val EMPTY_VALUE = ""
    const val NO_VALUE = "-"
    const val NEXT_VALUE_SEPARATOR = ", "
    val DEFAULT_LOCATION = LatLng(40.4169019, -3.7056721)

    fun getAdapter(
        context: Context,
        data: List<String>,
        firstOptionEnabled: Boolean = false
    ): SpinnerAdapter {

        val arrayAdapter = SpinnerAdapter(context, data, firstOptionEnabled)
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        return arrayAdapter
    }

    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }

    fun showOrHidePassword(editText: EditText, imageButton: ImageButton) {

        if (editText.transformationMethod is HideReturnsTransformationMethod) {

            imageButton.setImageResource(R.drawable.ic_show_password)
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {

            imageButton.setImageResource(R.drawable.ic_hide_password)
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
    }
}

object State {
    const val PENDING_STATE = "PENDING"
    const val IN_PROGRESS_STATE = "IN_PROGRESS"
    const val FINISHED_STATE = "FINISHED"
}

object Notifications {
    const val CHANNEL_ID = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_ID"
    const val CHANNEL_GROUP = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_GROUP"
}

enum class StatusBarStyle {
    PRIMARY,
    SECONDARY
}