package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.apiService.SongAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler

class SongAPIClient {

    //region Private properties
    private val api = APIClient.retrofit.create(SongAPIService::class.java)
    //endregion

    //region Public methods
    fun createSong(
        gameId: Int,
        song: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.createSong(headers, gameId, song)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }

    fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = SharedPreferencesHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = SharedPreferencesHandler.getCredentials().token
        val request = api.deleteSong(headers, gameId, songId)

        APIClient.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}