package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.Song

interface SongRepository {
    suspend fun createSong(gameId: Int, newSong: Song): Song
    suspend fun deleteSong(gameId: Int, songId: Int)
    suspend fun insertSongDatabase(song: Song)
}