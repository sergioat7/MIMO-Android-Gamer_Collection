package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Song

interface SongRepository {
    suspend fun createSong(
        gameId: Int,
        newSong: Song,
        success: (Song) -> Unit,
        failure: (ErrorModel) -> Unit
    )

    suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorModel) -> Unit
    )
}