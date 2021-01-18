package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.network.apiService.SongAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlin.collections.HashMap

class SongAPIClient(
    private val resources: Resources,
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    private val api = APIClient.getRetrofit(sharedPrefHandler).create(SongAPIService::class.java)

    fun createSong(gameId: Int, song: SongResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.createSong(headers, gameId, song)

        APIClient.sendServer<Void, ErrorResponse>(sharedPrefHandler, resources, request, {
            success()
        }, {
            failure(it)
        })
    }

    fun deleteSong(gameId: Int, songId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.deleteSong(headers, gameId, songId)

        APIClient.sendServer<Void, ErrorResponse>(sharedPrefHandler, resources, request, {
            success()
        }, {
            failure(it)
        })
    }
}