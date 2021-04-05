package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.apiService.SongAPIService
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper

class SongAPIClient {

    //region Private properties
    private val api = ApiManager.getService<SongAPIService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun createSong(
        gameId: Int,
        song: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val headers: MutableMap<String, String> = HashMap()
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.createSong(headers, gameId, song)

        ApiManager.sendServer(request, {
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
        headers[ApiManager.AUTHORIZATION_HEADER] = SharedPreferencesHelper.getCredentials().token
        val request = api.deleteSong(headers, gameId, songId)

        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}