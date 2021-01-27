package es.upsa.mimo.gamercollection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
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
        const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
        const val AUTHORIZATION_HEADER = "Authorization"

        // MARK: - Platform constants

        const val DEFAULT_PLATFORM = "OTHER"

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

                    Log.e("Constants", e.message ?: "")
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

                    Log.e("Constants", e.message ?: "")
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
    }
}