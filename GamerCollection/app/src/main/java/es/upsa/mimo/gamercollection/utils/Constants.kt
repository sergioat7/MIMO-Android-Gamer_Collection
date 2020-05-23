package es.upsa.mimo.gamercollection.utils

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // MARK: - SharedPref constants

        const val preferencesName = "preferences"
        const val userDataPrefName = "userData"
        const val authDataPrefName = "authData"

        // MARK: - Retrofit constants

        const val baseEndpoint = "https://videogames-collection-services.herokuapp.com/"
        const val acceptLanguageHeader = "Accept-Language"
        const val authorizationHeader = "Authorization"

        // MARK: - State constants

        const val pending = "PENDING"
        const val inProgress = "IN_PROGRESS"
        const val finished = "FINISHED"

        // MARK: Date format

        fun getDateFormat(): String {
            return if (Locale.getDefault().language == "es") "dd-MM-yyyy" else "MM-dd-yyyy"
        }

        fun dateToString(date: Date?): String? {

            date?.let {
                return try {
                    SimpleDateFormat(getDateFormat()).format(it)
                } catch (e: Exception) {
                    null
                }
            } ?: run {
                return null
            }
        }

        fun stringToDate(dateString: String?): Date? {

            dateString?.let {
                return try {
                    SimpleDateFormat(getDateFormat()).parse(it)
                } catch (e: Exception) {
                    null
                }
            } ?: run {
                return null
            }
        }

        // MARK: Game ordering

        const val defaultSortingKey = "name"
    }
}