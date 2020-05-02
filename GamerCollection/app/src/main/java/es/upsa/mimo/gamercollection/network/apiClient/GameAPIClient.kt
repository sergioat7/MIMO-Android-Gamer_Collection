package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.apiService.GameAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import kotlin.collections.HashMap

class GameAPIClient {
    companion object {

        fun getGames(sharedPrefHandler: SharedPreferencesHandler, resources: Resources, success: (List<GameResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
            headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
            val api = APIClient.getRetrofit().create(GameAPIService::class.java)
            val request = api.getGames(headers)

            APIClient.sendServer<List<GameResponse>, ErrorResponse>(resources, request, { games ->
                success(games)
            }, { errorResponse ->
                failure(errorResponse)
            })
        }
    }
}