package es.upsa.mimo.gamercollection.utils

import es.upsa.mimo.gamercollection.models.GameResponse
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

        fun getDateFormat(sharedPrefHandler: SharedPreferencesHandler): String {
            return if (sharedPrefHandler.getLanguage() == "es") "dd-MM-yyyy" else "MM-dd-yyyy"
        }

        fun dateToString(date: Date?, sharedPrefHandler: SharedPreferencesHandler): String? {

            date?.let {
                return try {
                    SimpleDateFormat(getDateFormat(sharedPrefHandler)).format(it)
                } catch (e: Exception) {
                    null
                }
            } ?: run {
                return null
            }
        }

        fun stringToDate(dateString: String?, sharedPrefHandler: SharedPreferencesHandler): Date? {

            dateString?.let {
                return try {
                    SimpleDateFormat(getDateFormat(sharedPrefHandler)).parse(it)
                } catch (e: Exception) {
                    null
                }
            } ?: run {
                return null
            }
        }

        // MARK: Game ordering

        fun orderGamesBy(games: List<GameResponse>, sortingKey: String): List<GameResponse> {

            return when(sortingKey) {
                "platform" -> games.sortedBy { it.platform }
                "releaseDate" -> games.sortedBy { it.releaseDate }
                "purchaseDate" -> games.sortedBy { it.purchaseDate }
                "price" -> games.sortedBy { it.price }
                else -> games.sortedBy { it.name }
            }
        }
    }
}