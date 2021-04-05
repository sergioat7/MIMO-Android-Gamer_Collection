package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.rawg.RawgGameListResponse
import es.upsa.mimo.gamercollection.models.rawg.RawgGameResponse
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.network.apiService.GameAPIService
import es.upsa.mimo.gamercollection.network.apiService.RawgGameApiService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class GameAPIClient {

    //region Private properties
    private val api = ApiManager.getService<GameAPIService>(ApiManager.BASE_ENDPOINT)
    private val apiRawg = ApiManager.getService<RawgGameApiService>(ApiManager.BASE_ENDPOINT_RAWG)
    //endregion

    //region Public methods
    fun getGames(success: (List<GameResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.getGames(headers)

        ApiManager.sendServer(request, success, failure)
    }

    fun getGame(gameId: Int, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.getGame(headers, gameId)

        ApiManager.sendServer(request, success, failure)
    }

    fun createGame(game: GameResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.createGame(headers, game)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }

    fun setGame(
        game: GameResponse,
        success: (GameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.setGame(headers, game.id, game)

        ApiManager.sendServer(request, success, failure)
    }

    fun deleteGame(gameId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHelper.getLanguage()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.deleteGame(headers, gameId)

        ApiManager.sendServer(request, {
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
        params[ApiManager.KEY_PARAM] = ApiManager.KEY_VALUE
        params[ApiManager.PAGE_PARAM] = page.toString()
        params[ApiManager.PAGE_SIZE_PARAM] = ApiManager.PAGE_SIZE.toString()
        query?.let {
            params[ApiManager.SEARCH_PARAM] = it
        }
        val request = apiRawg.getGames(params)

        ApiManager.sendServer(request, success, failure)
    }

    fun getRawgGame(
        gameId: Int,
        success: (RawgGameResponse) -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val params: MutableMap<String, String> = java.util.HashMap()
        params[ApiManager.KEY_PARAM] = ApiManager.KEY_VALUE
        val request = apiRawg.getGame(gameId, params)

        ApiManager.sendServer(request, success, failure)
    }
    //endregion
}