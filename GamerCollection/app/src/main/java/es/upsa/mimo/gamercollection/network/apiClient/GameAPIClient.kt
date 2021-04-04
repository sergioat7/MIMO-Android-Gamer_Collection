package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.rawg.RawgGameListResponse
import es.upsa.mimo.gamercollection.models.rawg.RawgGameResponse
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.network.apiService.GameAPIService
import es.upsa.mimo.gamercollection.network.apiService.RawgGameApiService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler

class GameAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(GameAPIService::class.java)
    private val apiRawg = APIClient.retrofitRawg.create(RawgGameApiService::class.java)
    //endregion

    //region Public methods
    fun getGames(success: (List<GameResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.getGames(headers)

        APIClient.sendServer(request, success, failure)
    }

    fun getGame(gameId: Int, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.getGame(headers, gameId)

        APIClient.sendServer(request, success, failure)
    }

    fun createGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.createGame(headers, game)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun setGame(
        game: GameResponse,
        success: (GameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.setGame(headers, game.id, game)

        APIClient.sendServer(request, success, failure)
    }

    fun deleteGame(gameId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.deleteGame(headers, gameId)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun getRawgGames(
        page: Int,
        query: String?,
        success: (RawgGameListResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val params: MutableMap<String, String> = java.util.HashMap()
        params[Constants.KEY_PARAM] = Constants.KEY_VALUE
        params[Constants.PAGE_PARAM] = page.toString()
        params[Constants.PAGE_SIZE_PARAM] = Constants.PAGE_SIZE.toString()
        query?.let {
            params[Constants.SEARCH_PARAM] = it
        }
        val request = apiRawg.getGames(params)

        APIClient.sendServer(request, success, failure)
    }

    fun getRawgGame(
        gameId: Int,
        success: (RawgGameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val params: MutableMap<String, String> = java.util.HashMap()
        params[Constants.KEY_PARAM] = Constants.KEY_VALUE
        val request = apiRawg.getGame(gameId, params)

        APIClient.sendServer(request, success, failure)
    }
    //endregion
}