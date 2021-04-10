package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.network.SongApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val api: SongApiService,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    //endregion

    //region Public methods
    fun createSong(
        gameId: Int,
        song: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.createSong(gameId, song))) {
                is RequestResult.Success -> success()
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        externalScope.launch {

            when (val response = ApiManager.validateResponse(api.deleteSong(gameId, songId))) {
                is RequestResult.Success -> success()
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }
    //endregion
}