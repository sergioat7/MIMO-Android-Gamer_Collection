package es.upsa.mimo.gamercollection.utils

import android.annotation.SuppressLint
import android.content.Context
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.persistence.repositories.*
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // MARK: - Database constants

        const val databaseName = "GamerCollection"

        fun manageFormats(context: Context, formats: List<FormatResponse>) {

            val formatRepository = FormatRepository(context)
            for (format in formats) {
                formatRepository.insertFormat(format)
            }
            formatRepository.removeDisableContent(formats)
        }

        fun manageGenres(context: Context, genres: List<GenreResponse>) {

            val genreRepository = GenreRepository(context)
            for (genre in genres) {
                genreRepository.insertGenre(genre)
            }
            genreRepository.removeDisableContent(genres)
        }

        fun managePlatforms(context: Context, platforms: List<PlatformResponse>) {

            val platformRepository = PlatformRepository(context)
            for (platform in platforms) {
                platformRepository.insertPlatform(platform)
            }
            platformRepository.removeDisableContent(platforms)
        }

        fun manageStates(context: Context, states: List<StateResponse>) {

            val stateRepository = StateRepository(context)
            for (state in states) {
                stateRepository.insertState(state)
            }
            stateRepository.removeDisableContent(states)
        }

        fun manageGames(context: Context, games: List<GameResponse>) {

            val gameRepository = GameRepository(context)
            for (game in games) {
                gameRepository.insertGame(game)
            }
            gameRepository.removeDisableContent(games)
        }

        fun manageSagas(context: Context, sagas: List<SagaResponse>) {

            val sagaRepository = SagaRepository(context)
            for (saga in sagas) {
                sagaRepository.insertSaga(saga)
            }
            sagaRepository.removeDisableContent(sagas)
        }

        // MARK: - SharedPref constants

        const val preferencesName = "preferences"
        const val userDataPrefName = "userData"
        const val authDataPrefName = "authData"
        const val languagePrefName = "language"
        const val sortingKeyPrefName = "sorting_key"
        const val swipeRefreshPrefName = "swipe_refresh_enabled"
        const val gameNotificationPrefName = "game_notification_launched_"

        // MARK: - Retrofit constants

        const val baseEndpoint = "https://videogames-collection-services.herokuapp.com/"
        const val acceptLanguageHeader = "Accept-Language"
        const val authorizationHeader = "Authorization"

        // MARK: - State constants

        const val pending = "PENDING"
        const val inProgress = "IN_PROGRESS"
        const val finished = "FINISHED"

        // MARK: - Spinner adapter

        fun getAdapter(context: Context, data: List<String>, firstOptionEnabled: Boolean = false): SpinnerAdapter {

            val arrayAdapter = SpinnerAdapter(context, data, firstOptionEnabled)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            return arrayAdapter
        }

        // MARK: Date format

        fun getDateFormat(sharedPrefHandler: SharedPreferencesHandler): String {
            return if (sharedPrefHandler.getLanguage() == "es") "dd-MM-yyyy" else "MM-dd-yyyy"
        }

        @SuppressLint("SimpleDateFormat")
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

        @SuppressLint("SimpleDateFormat")
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

        // MARK: Notifications

        const val channelId = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_ID"

        const val channelGroup = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_GROUP"
    }
}