package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.SongApiService

class SongAPIClient {

    //region Private properties
    private val api = ApiManager.getService<SongApiService>(ApiManager.BASE_ENDPOINT)
    //endregion

    //region Public methods
    fun createSong(
        gameId: Int,
        song: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {

        val request = api.createSong(gameId, song)
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

        val request = api.deleteSong(gameId, songId)
        ApiManager.sendServer(request, {
            success()
        }, failure)
    }
    //endregion
}