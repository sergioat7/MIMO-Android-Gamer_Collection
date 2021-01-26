package es.upsa.mimo.gamercollection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

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
        const val SWIPE_REFRESH_PREFERENCES_NAME = "swipeRefreshEnabled"
        const val GAME_NOTIFICATION_PREFERENCES_NAME = "gameNotificationLaunched_"

        // MARK: - Retrofit constants

        const val BASE_ENDPOINT = "https://videogames-collection-services.herokuapp.com/"
        const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
        const val AUTHORIZATION_HEADER = "Authorization"

        // MARK: - State constants

        const val PENDING_STATE = "PENDING"
        const val IN_PROGRESS_STATE = "IN_PROGRESS"
        const val FINISHED_STATE = "FINISHED"

        // MARK: - Spinner adapter

        fun getAdapter(context: Context, data: List<String>, firstOptionEnabled: Boolean = false): SpinnerAdapter {

            val arrayAdapter = SpinnerAdapter(context, data, firstOptionEnabled)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            return arrayAdapter
        }

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
    }
}