package es.upsa.mimo.gamercollection.data

import es.upsa.mimo.gamercollection.data.local.daos.SongDao
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.model.Song
import es.upsa.mimo.gamercollection.domain.toLocalData
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
) : SongRepository {

    //region Public methods
    override suspend fun createSong(gameId: Int, newSong: Song): Song =
        newSong.copy(id = getNextId()).also {
            songDao.insertSong(it.toLocalData())
        }

    override suspend fun deleteSong(gameId: Int, songId: Int) {
        songDao.getSong(songId)?.let {
            songDao.deleteSong(it)
        }
    }

    override suspend fun insertSongDatabase(song: Song) {
        songDao.insertSong(song.toLocalData())
    }
    //endregion

    //region Private methods
    private suspend fun getNextId(): Int {
        val songs = songDao.getSongs()
        return if (songs.isNotEmpty()) {
            songs.maxOf { it.id } + 1
        } else {
            0
        }
    }
    //endregion
}