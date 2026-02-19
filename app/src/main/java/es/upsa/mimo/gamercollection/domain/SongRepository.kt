package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SongResponse

interface SongRepository {
    suspend fun createSong(
        gameId: Int,
        newSong: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    )

    suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    )
}