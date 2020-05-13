package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiService.SongAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import kotlin.collections.HashMap

class SongAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit().create(SongAPIService::class.java)

    fun deleteSong(gameId: Int, songId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        headers[Constants.authorizationHeader] = sharedPrefHandler.getCredentials().token
        val request = api.deleteSong(headers, gameId, songId)

        APIClient.sendServerWithVoidResponse<ErrorResponse>(resources, request, {
            success()
        }, { errorResponse ->
            failure(errorResponse)
        })
    }
}