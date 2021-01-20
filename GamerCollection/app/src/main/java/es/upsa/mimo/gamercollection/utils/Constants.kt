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

        const val DATABASE_NAME = "GamerCollection"

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

        const val PREFERENCES_NAME = "preferences"
        const val USER_DATA_PREFERENCES_NAME = "userData"
        const val AUTH_DATA_PREFERENCES_NAME = "authData"
        const val LANGUAGE_PREFERENCES_NAME = "language"
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
                "score" -> games.sortedBy { it.score }
                else -> games.sortedBy { it.name }
            }
        }

        // MARK: Notifications

        const val CHANNEL_ID = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_ID"
        const val CHANNEL_GROUP = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_GROUP"
    }
}