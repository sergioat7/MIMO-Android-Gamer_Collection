package es.upsa.mimo.gamercollection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // MARK: - General

        const val POPUP_DIALOG = "popupDialog"
        const val LOADING_DIALOG = "loadingDialog"
        const val SYNC_DIALOG = "syncDialog"
        const val EMPTY_VALUE = ""
        const val POINT_UP = 0f
        const val POINT_DOWN = -180f

        fun getAdapter(context: Context, data: List<String>, firstOptionEnabled: Boolean = false): SpinnerAdapter {

            val arrayAdapter = SpinnerAdapter(context, data, firstOptionEnabled)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            return arrayAdapter
        }

        fun isDarkMode(context: Context?): Boolean {

            val mode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
            return mode == Configuration.UI_MODE_NIGHT_YES
        }

        // MARK: - Database constants

        const val DATABASE_NAME = "GamerCollection"

        // MARK: - SharedPref constants

        const val PREFERENCES_NAME = "preferences"
        const val USER_DATA_PREFERENCES_NAME = "userData"
        const val AUTH_DATA_PREFERENCES_NAME = "authData"
        const val LANGUAGE_PREFERENCES_NAME = "language"
        const val ENGLISH_LANGUAGE_KEY = "en"
        const val SPANISH_LANGUAGE_KEY = "es"
        const val SORTING_KEY_PREFERENCES_NAME = "sortingKey"
        const val DEFAULT_SORTING_KEY = "name"
        const val SWIPE_REFRESH_PREFERENCES_NAME = "swipeRefreshEnabled"
        const val GAME_NOTIFICATION_PREFERENCES_NAME = "gameNotificationLaunched_"
        const val VERSION_PREFERENCE_NAME = "version"

        // MARK: - Retrofit constants

        const val BASE_ENDPOINT = "https://videogames-collection-services.herokuapp.com/"
        const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"
        const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
        const val AUTHORIZATION_HEADER = "Authorization"
        const val OTHER_VALUE = "OTHER"
        const val KEY_PARAM = "key"
        const val KEY_VALUE = "747a7639039d4134a4370852b0f6b282"
        const val PAGE_PARAM = "page"
        const val PAGE_SIZE_PARAM = "page_size"
        const val PAGE_SIZE = 20
        const val SEARCH_PARAM = "search"

        // MARK: - State constants

        const val PENDING_STATE = "PENDING"
        const val IN_PROGRESS_STATE = "IN_PROGRESS"
        const val FINISHED_STATE = "FINISHED"

        // MARK: Date format

        const val DATE_FORMAT = "yyyy-MM-dd"

        fun getDateFormatToShow(language: String): String {

            return when(language) {
                SPANISH_LANGUAGE_KEY -> "d MMMM yyyy"
                else -> "MMMM d, yyyy"
            }
        }

        fun getFilterDateFormat(language: String): String {

            return when(language) {
                SPANISH_LANGUAGE_KEY -> "dd/MM/yyyy"
                else -> "MM/dd/yyyy"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun dateToString(date: Date?,
                         format: String? = null,
                         language: String? = null): String? {

            val dateFormat = format ?: DATE_FORMAT
            val locale = language?.let {
                Locale.forLanguageTag(it)
            } ?: run {
                Locale.getDefault()
            }
            date?.let {

                return try {
                    SimpleDateFormat(dateFormat, locale).format(it)
                } catch (e: Exception) {

                    Log.e("Constants", e.message ?: Constants.EMPTY_VALUE)
                    null
                }
            } ?: run {

                Log.e("Constants", "date null")
                return null
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun stringToDate(dateString: String?,
                         format: String? = null,
                         language: String? = null): Date? {

            val dateFormat = format ?: DATE_FORMAT
            val locale = language?.let {
                Locale.forLanguageTag(it)
            } ?: run {
                Locale.getDefault()
            }
            dateString?.let {

                return try {
                    SimpleDateFormat(dateFormat, locale).parse(it)
                } catch (e: Exception) {

                    Log.e("Constants", e.message ?: Constants.EMPTY_VALUE)
                    null
                }
            } ?: run {
                Log.e("Constants", "dateString null")
                return null
            }
        }

        // MARK: Notifications

        const val CHANNEL_ID = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_ID"
        const val CHANNEL_GROUP = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_GROUP"

        // MARK: Game

        const val GAME_ID = "gameId"
        const val NO_VALUE = "-"

        fun getPegiImage(pegi: String?, context: Context): Drawable? {

            return when(pegi) {
                "+3" -> ContextCompat.getDrawable(context, R.drawable.pegi3)
                "+4" -> ContextCompat.getDrawable(context, R.drawable.pegi4)
                "+6" -> ContextCompat.getDrawable(context, R.drawable.pegi6)
                "+7" -> ContextCompat.getDrawable(context, R.drawable.pegi7)
                "+12" -> ContextCompat.getDrawable(context, R.drawable.pegi12)
                "+16" -> ContextCompat.getDrawable(context, R.drawable.pegi16)
                "+18" -> ContextCompat.getDrawable(context, R.drawable.pegi18)
                else -> null
            }
        }

        // MARK: - Saga

        const val SAGA_ID = "sagaId"

        // MARK: - Maps

        val DEFAULT_LOCATION = LatLng(40.4169019, -3.7056721)

        // MARK: - Login constants

        fun isUserNameValid(username: String): Boolean {
            return username.isNotBlank()
        }

        fun isPasswordValid(password: String): Boolean {
            return password.length > 3
        }

        fun showOrHidePassword(editText: EditText, imageButton: ImageButton, isDarkMode: Boolean) {

            if (editText.transformationMethod is HideReturnsTransformationMethod) {

                val image = if (isDarkMode) R.drawable.ic_show_password_dark else R.drawable.ic_show_password_light
                imageButton.setImageResource(image)
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {

                val image = if (isDarkMode) R.drawable.ic_hide_password_dark else R.drawable.ic_hide_password_light
                imageButton.setImageResource(image)
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }
    }
}