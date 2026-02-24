package es.upsa.mimo.gamercollection.data

import es.upsa.mimo.gamercollection.data.di.IoDispatcher
import es.upsa.mimo.gamercollection.data.local.daos.SongDao
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.data.remote.interfaces.SongApiService
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Song
import es.upsa.mimo.gamercollection.domain.toRemoteData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val api: SongApiService,
    private val songDao: SongDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SongRepository {

    //region Private properties
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    override suspend fun createSong(
        gameId: Int,
        newSong: Song,
        success: (Song) -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        val song = newSong.copy(id = getNextId())
        insertSongDatabase(song)
        success(song)
//        try {
//            when (val response = ApiManager.validateResponse(api.createSong(gameId, newSong))) {
//
//                is RequestResult.Success -> success()
//                is RequestResult.Failure -> failure(response.error)
//                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//            }
//        } catch (e: Exception) {
//            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//        }
    }

    override suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorModel) -> Unit
    ) {
        getSongDatabase(songId)?.let {
            deleteSongDatabase(it)
        }
        success()
//        try {
//            when (val response = ApiManager.validateResponse(api.deleteSong(gameId, songId))) {
//
//                is RequestResult.Success -> success()
//                is RequestResult.Failure -> failure(response.error)
//                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//            }
//        } catch (e: Exception) {
//            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//        }
    }

    override fun insertSongDatabase(song: Song) {

        runBlocking {
            val job = databaseScope.launch {
                songDao.insertSong(song.toRemoteData())
            }
            job.join()
        }
    }
    //endregion

    //region Private methods
    private fun getSongsDatabase(): List<SongResponse> {

        var songs: List<SongResponse> = arrayListOf()
        runBlocking {

            val result = databaseScope.async {
                songDao.getSongs()
            }
            songs = result.await()
        }
        return songs
    }

    private fun getSongDatabase(songId: Int): SongResponse? {

        var song: SongResponse? = null
        runBlocking {

            val result = databaseScope.async {
                songDao.getSong(songId)
            }
            song = result.await()
        }
        return song
    }

    private fun deleteSongDatabase(song: SongResponse) {

        runBlocking {
            val job = databaseScope.launch {
                songDao.deleteSong(song)
            }
            job.join()
        }
    }

    private fun getNextId(): Int {

        val songs = getSongsDatabase()
        return if (songs.isNotEmpty()) {
            songs.maxOf { it.id } + 1
        } else {
            0
        }
    }
    //endregion
}