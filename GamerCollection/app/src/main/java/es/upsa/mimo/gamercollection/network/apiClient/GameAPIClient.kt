package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.apiService.GameAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlin.collections.HashMap

class GameAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit(sharedPrefHandler).create(GameAPIService::class.java)

    fun getGames(success: (List<GameResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.getGames(headers)

        APIClient.sendServer<List<GameResponse>, ErrorResponse>(sharedPrefHandler, resources, request, {
            success(it)
        }, {
            failure(it)
        })
    }

    fun getGame(gameId: Int, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.getGame(headers, gameId)

        APIClient.sendServer<GameResponse, ErrorResponse>(sharedPrefHandler, resources, request, {
            success(it)
        }, {
            failure(it)
        })
    }

    fun createGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.createGame(headers, game)

        APIClient.sendServer<Void, ErrorResponse>(sharedPrefHandler, resources, request, {
            success()
        }, {
            failure(it)
        })
    }

    fun setGame(game: GameResponse, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.setGame(headers, game.id, game)

        APIClient.sendServer<GameResponse, ErrorResponse>(sharedPrefHandler, resources, request, {
            success(it)
        }, {
            failure(it)
        })
    }

    fun deleteGame(gameId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = sharedPrefHandler.getLanguage()
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.deleteGame(headers, gameId)

        APIClient.sendServer<Void, ErrorResponse>(sharedPrefHandler, resources, request, {
            success()
        }, {
            failure(it)
        })
    }
}