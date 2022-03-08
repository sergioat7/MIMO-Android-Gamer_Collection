package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.network.SongApiService
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val api: SongApiService
) {

    //region Public methods
    suspend fun createSong(
        gameId: Int,
        newSong: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        try {
            when (val response = ApiManager.validateResponse(api.createSong(gameId, newSong))) {

                is RequestResult.Success -> success()
                is RequestResult.Failure -> failure(response.error)
                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
            }
        } catch (e: Exception) {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
        }
    }

    suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        try {
            when (val response = ApiManager.validateResponse(api.deleteSong(gameId, songId))) {

                is RequestResult.Success -> success()
                is RequestResult.Failure -> failure(response.error)
                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
            }
        } catch (e: Exception) {
            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
        }
    }
    //endregion
}