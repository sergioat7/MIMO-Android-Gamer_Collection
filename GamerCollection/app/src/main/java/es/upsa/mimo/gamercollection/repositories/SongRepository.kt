package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.network.SongApiService
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val api: SongApiService
) {

    //region Public methods
    suspend fun createSong(
        gameId: Int,
        song: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        when (val response = ApiManager.validateResponse(api.createSong(gameId, song))) {

            is RequestResult.Success -> success()
            is RequestResult.Failure -> failure(response.error)
        }
    }

    suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        when (val response = ApiManager.validateResponse(api.deleteSong(gameId, songId))) {

            is RequestResult.Success -> success()
            is RequestResult.Failure -> failure(response.error)
        }
    }
    //endregion
}