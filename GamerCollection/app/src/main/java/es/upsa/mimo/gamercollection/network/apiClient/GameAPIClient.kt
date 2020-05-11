package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.apiService.GameAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import kotlin.collections.HashMap

class GameAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit().create(GameAPIService::class.java)

    fun getGames(success: (List<GameResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.getGames(headers)

        APIClient.sendServer<List<GameResponse>, ErrorResponse>(resources, request, { games ->
            success(games)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }

    fun setGame(game: GameResponse, success: (GameResponse) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.setGame(headers, game.id, game)

        APIClient.sendServer<GameResponse, ErrorResponse>(resources, request, { game ->
            success(game)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }

    fun deleteGame(gameId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.deleteUser(headers, gameId)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, { errorResponse ->
            failure(errorResponse)
        })
    }
}